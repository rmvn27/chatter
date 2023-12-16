package chatter.domain.services.live.connection

import arrow.core.raise.either
import chatter.domain.services.live.RoomService
import chatter.domain.services.teams.MessageService
import chatter.lib.coroutines.Locked
import chatter.models.Message
import chatter.models.UserPrincipal
import chatter.models.WsCommand
import chatter.models.WsEvent

// handler for a connection that:
// - receives and handles the incoming commands
// - sends events to the client
// - tracks the state of the client
class ClientConnectionHandler(
    val user: UserPrincipal,
    private val conn: ClientConnection,
    private val roomService: RoomService,
    private val messageService: MessageService
) {
    private val state = Locked<ClientConnectionState>(ClientConnectionState.Base)

    suspend fun sendMessage(message: Message) {
        conn.send(WsEvent.Message(message))
    }

    fun handleClose() = conn.onClosed {
        removeFromOldTeam()
    }

    // handle commands at a centralised place and not in the rooms
    // otherwise there were some problems with the state of the connections
    suspend fun handleCommands() = conn.handleCommands {
        when (it) {
            is WsCommand.EnterTeam -> enterTeam(it.teamSlug, null)
            is WsCommand.EnterChannel -> enterChannel(it.channelSlug)
            is WsCommand.EnterTeamAndChannel -> enterTeam(it.teamSlug, it.channelSlug)
            is WsCommand.LeaveTeam -> removeFromOldTeam()
            is WsCommand.LeaveChannel -> removeFromOldChannel()
            is WsCommand.SendTextMessage -> sendTextMessage(it.message)
            else -> {}
        }
    }

    private suspend fun enterTeam(teamSlug: String, channelSlug: String?) = either {
        removeFromOldTeam()

        val room = roomService.getTeamRoomBySlug(teamSlug).bind()
        val channelRoom = room.addConn(this@ClientConnectionHandler, channelSlug).bind()

        state.update {
            if (channelRoom == null) {
                ClientConnectionState.InTeam(room.teamId)
            } else {
                ClientConnectionState.InTeamAndChannel(room.teamId, channelRoom.channelId)
            }
        }
    }

    private suspend fun enterChannel(channelSlug: String) = either {
        removeFromOldChannel()

        val teamId = state.get().teamId ?: return@either
        val team = roomService.getTeamRoom(teamId)
        val channelRoom = team.addConnToChannel(this@ClientConnectionHandler, channelSlug).bind()

        state.update {
            ClientConnectionState.InTeamAndChannel(teamId, channelRoom.channelId)
        }
    }

    private suspend fun removeFromOldChannel() {
        val (teamId, channelId) = state.withLock {
            val teamId = it.teamId ?: return@withLock null
            val channelId = it.channelId ?: return@withLock null

            Pair(teamId, channelId)
        } ?: return

        val team = roomService.getTeamRoom(teamId)
        val channelRoom = team.getChannelRoom(channelId)
        channelRoom.removeConn(this)

        state.update {
            ClientConnectionState.InTeam(teamId)
        }
    }

    private suspend fun removeFromOldTeam() {
        val currentState = state.get()

        val teamId = currentState.teamId ?: return
        val team = roomService.getTeamRoom(teamId)
        team.removeConn(this, currentState.channelId)

        state.update { ClientConnectionState.Base }
    }

    private suspend fun sendTextMessage(message: String) {
        val (teamId, channelId) = state.withLock {
            val teamId = it.teamId ?: return@withLock null
            val channelId = it.channelId ?: return@withLock null

            Pair(teamId, channelId)
        } ?: return

        messageService.sendMessage(
            teamId = teamId,
            channelId = channelId,
            user = user,
            msg = message
        )
    }
}
