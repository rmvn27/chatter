package chatter.domain.services.live.rooms

import arrow.core.raise.either
import chatter.TeamEntity
import chatter.domain.services.auth.AuthorizationService
import chatter.domain.services.live.connection.ClientConnectionHandler
import chatter.domain.services.teams.ChannelService
import chatter.domain.services.teams.MessageService
import chatter.lib.coroutines.Locked
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import java.util.UUID

class TeamRoom(
    private val team: TeamEntity,
    private val channelService: ChannelService,
    private val messageService: MessageService,
    private val authService: AuthorizationService
) {
    val teamId = team.id

    // each team has it's own coroutine scope for async actions
    // that is also shared with the rooms for a team
    private val scope = CoroutineScope(SupervisorJob())

    // associate the handlers with the job that handles the commands
    private val connections = Locked(mutableSetOf<ClientConnectionHandler>())
    private val channels = Locked(mutableMapOf<UUID, ChannelRoom>())

    suspend fun getChannelRoom(channelId: UUID) = channels.withLock {
        it.getOrPut(channelId) {
            ChannelRoom(
                teamId = teamId,
                channelId = channelId,
                scope,
                messageService
            )
        }
    }

    suspend fun addConn(conn: ClientConnectionHandler, channelSlug: String?) = either {
        authService.authorizeTeamOwnerOrParticipant(conn.user, team.slug).bind()

        connections.withLock { it.add(conn) }
        if (channelSlug == null) return@either null

        addConnToChannel(conn, channelSlug).bind()
    }

    suspend fun removeConn(conn: ClientConnectionHandler, channelId: UUID?) {
        connections.withLock { it.remove(conn) }

        if (channelId != null) {
            channels.withLock {
                val channelRoom = it[channelId]
                channelRoom?.removeConn(conn)
            }
        }
    }

    suspend fun addConnToChannel(conn: ClientConnectionHandler, channelSlug: String) = either {
        val channel = channelService.findChannelByTeamIdAndSlug(teamId, channelSlug).bind()
        val channelRoom = getChannelRoom(channel.id)
        channelRoom.addConn(conn)

        channelRoom
    }

    suspend fun close() {
        val job = scope.coroutineContext[Job]
        job?.cancelAndJoin()
    }
}
