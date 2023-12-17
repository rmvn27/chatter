package chatter.domain.services.live.connection

import arrow.core.raise.Raise
import arrow.core.raise.either
import chatter.errors.ApplicationError
import chatter.models.WsCommand
import chatter.models.WsEvent
import kotlinx.coroutines.flow.Flow

// create an abstraction to not leak the http layer
// into the domain layer
//
// the client connection should represent a connected live client to the server
interface ClientConnection {
    // flow of commands that can be received from the client
    fun commands(): Flow<WsCommand>

    // send events to the client
    suspend fun send(event: WsEvent)

    // await the close of the connection and then execute action
    fun onClosed(action: suspend () -> Unit)

    suspend fun close()
}

suspend fun ClientConnection.handleCommands(
    action: suspend Raise<ApplicationError>.(WsCommand) -> Unit
) = commands().collect {
    val result = either { action(it) }

    result.onLeft {
        send(WsEvent.Error(it.message, it.status.value))
    }
}
