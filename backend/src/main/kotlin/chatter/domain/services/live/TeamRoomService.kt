package chatter.domain.services.live

import arrow.core.raise.either
import chatter.domain.services.TeamChannelService
import chatter.domain.services.TeamMessageService
import chatter.domain.services.auth.AuthorizationService
import chatter.domain.services.live.client.ClientConnection
import chatter.domain.services.live.client.handleCommands
import chatter.domain.services.live.client.teamId
import chatter.domain.stores.TeamStore
import chatter.lib.Locked
import chatter.lib.app.AppScope
import chatter.lib.log.getValue
import chatter.lib.service.StatefulService
import chatter.models.WsCommand
import co.touchlab.kermit.Logger
import com.squareup.anvil.annotations.ContributesMultibinding
import com.squareup.anvil.annotations.optional.SingleIn
import java.util.UUID
import javax.inject.Inject

@SingleIn(AppScope::class)
@ContributesMultibinding(AppScope::class)
class TeamRoomService @Inject constructor(
    private val teamStore: TeamStore,
    private val authorizationService: AuthorizationService,
    private val channelService: TeamChannelService,
    private val messageService: TeamMessageService
) : StatefulService {
    private val logger by Logger

    private val teamRooms = Locked(mutableMapOf<UUID, TeamRoom>())

    suspend fun handle(conn: ClientConnection) {
        conn.onClosed {
            conn.removeFromOldTeam()
        }

        conn.handleCommands {
            when (it) {
                is WsCommand.EnterTeam -> conn.handleEnterTeam(it.teamSlug, null).bind()
                is WsCommand.EnterTeamAndChannel -> conn.handleEnterTeam(it.teamSlug, it.channelSlug).bind()
                is WsCommand.LeaveTeam -> conn.removeFromOldTeam()
                else -> {}
            }
        }
    }

    override suspend fun release() {
        logger.i { "Draining connections" }

        teamRooms.withLock { rooms ->
            rooms.values.forEach { it.close() }
            rooms.clear()
        }
    }

    private suspend fun ClientConnection.handleEnterTeam(teamSlug: String, channelSlug: String?) = either {
        // if the client was already in a team we need to remove the client from it
        //
        // when we just change the team we need to remove it from the previous
        // before connecting it to the new one
        //
        // and when we leave the current team then we just remove it
        removeFromOldTeam()

        // authorize the user before adding them to the room
        authorizationService.authorizeTeamOwnerOrParticipant(userId, teamSlug).bind()

        val team = teamStore.findBySlug(teamSlug).bind()
        val room = getTeamRoom(team.id)
        room.addClient(this@handleEnterTeam, channelSlug).bind()
    }

    // remove the client from the previous team
    private suspend fun ClientConnection.removeFromOldTeam() {
        val teamId = state().teamId ?: return

        teamRooms.withLock {
            val room = it[teamId] ?: return@withLock

            room.removeClient(this)
        }
    }

    private suspend fun getTeamRoom(teamId: UUID) = teamRooms.withLock {
        it.getOrPut(teamId) { TeamRoom(teamId, channelService, messageService) }
    }
}
