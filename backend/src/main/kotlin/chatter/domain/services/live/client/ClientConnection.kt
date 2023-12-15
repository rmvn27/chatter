package chatter.domain.services.live.client

import arrow.core.raise.Raise
import arrow.core.raise.either
import chatter.errors.ApplicationError
import chatter.models.WsCommand
import chatter.models.WsEvent
import kotlinx.coroutines.flow.Flow
import java.util.UUID


// create an abstraction to not leak the http layer
// into the domain layer
//
// the client connection should represent a connected live client to the server
interface ClientConnection {
    // id of the current connected user
    val userId: UUID

    suspend fun state(): ClientConnectionState
    suspend fun <T : ClientConnectionState> setState(newState: suspend (ClientConnectionState) -> T): T

    // flow of commands that can be received from the client
    fun commands(): Flow<WsCommand>

    // send events to the client
    suspend fun send(event: WsEvent)

    // await the close of the connection and then execute action
    fun onClosed(action: suspend () -> Unit)
}

suspend fun ClientConnection.handleCommands(
    action: suspend Raise<ApplicationError>.(WsCommand) -> Unit
) = commands().collect {
    val result = either { action(it) }

    result.onLeft {
        send(WsEvent.Error(it.message, it.status.value))
    }
}

suspend inline fun <reified T : WsCommand> ClientConnection.handleCommand(
    crossinline action: suspend Raise<ApplicationError>.(T) -> Unit
) {
    handleCommands {
        if (it is T) action(this, it)
    }
}
