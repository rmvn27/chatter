package chatter.domain.services.live.rooms

import chatter.domain.services.live.connection.ClientConnectionHandler
import chatter.domain.services.teams.MessageService
import chatter.lib.coroutines.Locked
import chatter.lib.coroutines.collectInScope
import chatter.models.Message
import chatter.models.WsEvent
import kotlinx.coroutines.CoroutineScope
import java.util.UUID

class ChannelRoom(
    val teamId: UUID,
    val channelId: UUID,
    scope: CoroutineScope,
    messageService: MessageService
) {
    // associate the connections with the job that handles the commands
    private val connections = Locked(mutableSetOf<ClientConnectionHandler>())

    init {
        messageService.liveMessages(teamId, channelId)
            .collectInScope(scope, ::sendMessage)
    }

    suspend fun addConn(conn: ClientConnectionHandler) {
        connections.withLock { it.add(conn) }
    }

    suspend fun removeConn(conn: ClientConnectionHandler) {
        connections.withLock { it.remove(conn) }
    }

    private suspend fun sendMessage(msg: Message) {
        connections.get().forEach { it.sendEvent(WsEvent.MessageReceived(msg)) }
    }
}
