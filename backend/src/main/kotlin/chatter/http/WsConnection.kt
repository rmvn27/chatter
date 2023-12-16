package chatter.http

import chatter.domain.services.live.connection.ClientConnection
import chatter.models.WsCommand
import chatter.models.WsEvent
import io.ktor.serialization.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.shareIn

// `ClientConnection` based on a Websocket
class WsConnection(
    private val session: WebSocketServerSession
) : ClientConnection {
    private val closeActions = mutableSetOf<suspend () -> Unit>()

    // make sure that the incoming channel can be consumed multiple times
    // for this we consume it as a flow and then crate a `SharedFlow` out of it
    private val commands: Flow<WsCommand> = session.incoming
        .receiveAsFlow()
        // we have set the converter in the config
        // so it's safe to access it
        .map { session.converter!!.deserialize<WsCommand>(it) }
        // execute all close actions when the flow was cancelled or closed
        .onCompletion {
            closeActions.forEach { it() }
            closeActions.clear()
        }
        // start collecting on the first consumer and do it until the flow is closed
        .shareIn(session, SharingStarted.Lazily)

    override fun commands() = commands
    override suspend fun send(event: WsEvent) = session.sendSerialized(event)


    // execute the action when the flow collection was closed
    // the session gives no other ways to detect this
    override fun onClosed(action: suspend () -> Unit) {
        closeActions.add(action)
    }

    override suspend fun close() = session.close()
}
