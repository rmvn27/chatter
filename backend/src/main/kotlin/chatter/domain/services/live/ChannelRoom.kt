package chatter.domain.services.live

import chatter.domain.services.TeamMessageService
import chatter.domain.services.live.client.ClientConnection
import chatter.domain.services.live.client.ClientConnectionState
import chatter.domain.services.live.client.handleCommand
import chatter.lib.Locked
import chatter.models.MessageEvent
import chatter.models.WsCommand
import chatter.models.WsEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.UUID

class ChannelRoom(
    private val teamId: UUID,
    private val channelId: UUID,
    private val scope: CoroutineScope,
    private val messageService: TeamMessageService
) : CoroutineScope by scope {
    // associate the connections with the job that handles the commands
    private val connections = Locked(mutableMapOf<ClientConnection, Job>())

    suspend fun sendMessage(event: MessageEvent) {
        connections.get().keys.forEach {
            it.send(
                WsEvent.Message(
                    content = event.message,
                    timestamp = event.timestamp,
                    userId = event.userId
                )
            )
        }
    }

    suspend fun addClient(conn: ClientConnection) {
        conn.setState { ClientConnectionState.InTeamAndChannel(teamId = teamId, channelId = channelId) }

        val handleJob = handle(conn)
        connections.withLock { it[conn] = handleJob }
    }

    suspend fun removeClient(conn: ClientConnection) {
        conn.setState { ClientConnectionState.InTeam(teamId) }

        connections.withLock { it.remove(conn)?.cancel() }
    }

    private fun handle(conn: ClientConnection): Job = launch {
        conn.handleCommand<WsCommand.SendTextMessage> {
            messageService.sendMessage(
                teamId = teamId,
                channelId = channelId,
                msg = it.message,
                userId = conn.userId
            )
        }
    }
}
