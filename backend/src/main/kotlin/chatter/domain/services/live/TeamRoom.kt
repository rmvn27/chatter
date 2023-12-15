package chatter.domain.services.live

import arrow.core.raise.either
import chatter.domain.services.TeamChannelService
import chatter.domain.services.TeamMessageService
import chatter.domain.services.live.client.ClientConnection
import chatter.domain.services.live.client.ClientConnectionState
import chatter.domain.services.live.client.channelId
import chatter.domain.services.live.client.handleCommands
import chatter.lib.Locked
import chatter.models.MessageEvent
import chatter.models.WsCommand
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.UUID

class TeamRoom(
    private val teamId: UUID,
    private val channelService: TeamChannelService,
    private val messageService: TeamMessageService
) : CoroutineScope {
    private val scope = CoroutineScope(SupervisorJob())
    override val coroutineContext = scope.coroutineContext

    // associate the connections with the job that handles the commands
    private val connections = Locked(mutableMapOf<ClientConnection, Job>())
    private val channels = Locked(mutableMapOf<UUID, ChannelRoom>())

    init {
        // when a room is created listen for it's liveMessages
        launch {
            messageService.liveMessages(teamId)
                .collectLatest(::handleMessage)
        }
    }

    suspend fun addClient(conn: ClientConnection, channelSlug: String?) = either {
        conn.setState { ClientConnectionState.InTeam(teamId) }

        if (channelSlug != null) conn.handleEnterChannel(channelSlug).bind()

        val handleJob = handle(conn)
        connections.withLock { it[conn] = handleJob }
    }

    suspend fun removeClient(conn: ClientConnection) {
        conn.setState { ClientConnectionState.Base }

        connections.withLock { it.remove(conn)?.cancel() }
        val channelId = conn.state().channelId ?: return
        channels.withLock {
            val channel = it[channelId]
            channel?.removeClient(conn)
        }
    }

    suspend fun close() {
        val job = coroutineContext[Job]
        job?.cancelAndJoin()
    }

    private suspend fun handleMessage(event: MessageEvent) {
        val room = channels.get()[event.channelId]
        room?.sendMessage(event)
    }

    private fun handle(conn: ClientConnection): Job = launch {
        conn.handleCommands {
            when (it) {
                is WsCommand.EnterChannel -> conn.handleEnterChannel(it.channelSlug)
                is WsCommand.LeaveChannel -> conn.removeFromOldChannel()
                else -> {}
            }
        }
    }

    private suspend fun ClientConnection.handleEnterChannel(channelSlug: String) = either {
        // remove from possible previous channel
        removeFromOldChannel()

        val channel = channelService.findChannelByTeamIdAndSlug(teamId, channelSlug).bind()
        val room = getChannelRoom(channel.id)

        room.addClient(this@handleEnterChannel)
    }

    // remove the client from the previous team
    private suspend fun ClientConnection.removeFromOldChannel() {
        val channelId = state().channelId ?: return

        channels.withLock {
            val channel = it[channelId]
            channel?.removeClient(this)
        }
    }

    private suspend fun getChannelRoom(channelId: UUID) = channels.withLock {
        it.getOrPut(channelId) {
            ChannelRoom(
                teamId = teamId,
                channelId = channelId,
                scope,
                messageService
            )
        }
    }
}
