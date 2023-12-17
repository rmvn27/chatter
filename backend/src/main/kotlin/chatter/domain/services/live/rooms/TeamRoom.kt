package chatter.domain.services.live.rooms

import arrow.core.raise.either
import chatter.TeamEntity
import chatter.db.display
import chatter.domain.services.auth.AuthorizationService
import chatter.domain.services.live.connection.ClientConnectionHandler
import chatter.domain.services.teams.ChannelService
import chatter.domain.services.teams.MessageService
import chatter.domain.services.teams.TeamEventsService
import chatter.lib.coroutines.Locked
import chatter.lib.coroutines.collectInScope
import chatter.lib.log.getValue
import chatter.models.ChannelListChangedEvent
import chatter.models.ParticipantListChangedEvent
import chatter.models.TeamEvent
import chatter.models.WsEvent
import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import java.util.UUID

class TeamRoom(
    private val team: TeamEntity,
    private val channelService: ChannelService,
    private val messageService: MessageService,
    private val authService: AuthorizationService,
    teamEventsService: TeamEventsService
) {
    // each team has it's own coroutine scope for async actions
    // that is also shared with the rooms for a team
    private val scope = CoroutineScope(SupervisorJob())

    // associate the handlers with the job that handles the commands
    private val connections = Locked(mutableSetOf<ClientConnectionHandler>())
    private val channels = Locked(mutableMapOf<UUID, ChannelRoom>())

    private val logger by Logger


    val teamId = team.id

    init {
        teamEventsService.eventsForTeam(team.id)
            .collectInScope(scope, ::handleTeamEvent)
    }

    suspend fun getChannelRoom(channelId: UUID) = channels.withLock {
        it.getOrPut(channelId) {
            val channel = channelService.findByIdInfallible(channelId)
            logger.d { "Creating new ChannelRoom for ${team.display()}-${channel.display()}" }

            ChannelRoom(
                team = team,
                channel = channel,
                scope,
                messageService
            )
        }
    }

    suspend fun addConn(conn: ClientConnectionHandler, channelSlug: String?) = either {
        authService.authorizeTeamOwnerOrParticipant(conn.user, team.slug).bind()

        connections.withLock { it.add(conn) }
        logger.d { "Adding ${conn.user.display()} to room for ${team.display()}" }

        if (channelSlug == null) return@either null

        addConnToChannel(conn, channelSlug).bind()
    }

    suspend fun removeConn(conn: ClientConnectionHandler, channelId: UUID?) {
        connections.withLock { it.remove(conn) }
        logger.d { "Removing ${conn.user.display()} from room for ${team.display()}" }

        if (channelId != null) {
            channels.withLock {
                val channelRoom = it[channelId]
                channelRoom?.removeConn(conn)
            }
        }
    }

    suspend fun addConnToChannel(conn: ClientConnectionHandler, channelSlug: String) = either {
        val channel = channelService.findChannelByTeamIdAndSlug(team.id, channelSlug).bind()
        val channelRoom = getChannelRoom(channel.id)
        channelRoom.addConn(conn)

        channelRoom
    }

    suspend fun close() {
        val job = scope.coroutineContext[Job]
        job?.cancelAndJoin()
    }

    private suspend fun handleTeamEvent(event: TeamEvent) {
        when (event) {
            is ChannelListChangedEvent -> sendEventToClients(event.toWsEvent())
            is ParticipantListChangedEvent -> sendEventToClients(event.toWsEvent())
        }
    }

    // send events to all clients in parallel
    private suspend fun sendEventToClients(event: WsEvent) = coroutineScope {
        connections.get().map {
            async { it.sendEvent(event) }
        }.awaitAll()
    }
}
