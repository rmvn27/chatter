package chatter.domain.services.live.rooms

import chatter.ChannelEntity
import chatter.TeamEntity
import chatter.db.display
import chatter.domain.services.live.connection.ClientConnectionHandler
import chatter.domain.services.teams.MessageService
import chatter.lib.coroutines.Locked
import chatter.lib.coroutines.collectInScope
import chatter.lib.log.getValue
import chatter.models.Message
import chatter.models.WsEvent
import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope

class ChannelRoom(
    val team: TeamEntity,
    val channel: ChannelEntity,
    scope: CoroutineScope,
    messageService: MessageService
) {
    // associate the connections with the job that handles the commands
    private val connections = Locked(mutableSetOf<ClientConnectionHandler>())

    private val logger by Logger

    val channelId = channel.id

    init {
        messageService.liveMessages(team.id, channel.id)
            .collectInScope(scope, ::sendMessage)
    }

    suspend fun addConn(conn: ClientConnectionHandler) {
        connections.withLock { it.add(conn) }

        logger.d { "Adding ${conn.user.display()} to room for ${team.display()}-${channel.display()}" }
    }

    suspend fun removeConn(conn: ClientConnectionHandler) {
        connections.withLock { it.remove(conn) }

        logger.d { "Removing ${conn.user.display()} from room for ${team.display()}-${channel.display()}" }
    }

    private suspend fun sendMessage(msg: Message) {
        connections.get().forEach { it.sendEvent(WsEvent.MessageReceived(msg)) }
    }
}
