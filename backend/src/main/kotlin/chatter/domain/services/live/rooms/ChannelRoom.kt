package chatter.domain.services.live.rooms

import chatter.domain.services.live.connection.ClientConnectionHandler
import chatter.domain.services.teams.MessageService
import chatter.lib.coroutines.Locked
import chatter.models.Message
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.UUID

class ChannelRoom(
    val teamId: UUID,
    val channelId: UUID,
    private val scope: CoroutineScope,
    private val messageService: MessageService
) {
    // associate the connections with the job that handles the commands
    private val connections = Locked(mutableSetOf<ClientConnectionHandler>())

    init {
        scope.launch {
            messageService.liveMessages(teamId, channelId).collect(::sendMessage)
        }
    }

    suspend fun addConn(conn: ClientConnectionHandler) {
        connections.withLock { it.add(conn) }
    }

    suspend fun removeConn(conn: ClientConnectionHandler) {
        connections.withLock { it.remove(conn) }
    }

    private suspend fun sendMessage(msg: Message) {
        connections.get().forEach { it.sendMessage(msg) }
    }
}
