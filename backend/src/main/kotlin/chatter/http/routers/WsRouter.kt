package chatter.http.routers

import arrow.core.Either
import chatter.domain.services.auth.AuthenticationService
import chatter.domain.services.live.RoomService
import chatter.domain.services.live.connection.ClientConnection
import chatter.domain.services.live.connection.ClientConnectionHandler
import chatter.domain.services.teams.MessageService
import chatter.http.WsConnection
import chatter.lib.app.AppScope
import chatter.lib.http.config.HttpRouter
import chatter.models.UserPrincipal
import chatter.models.WsCommand
import com.squareup.anvil.annotations.ContributesMultibinding
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import javax.inject.Inject

@ContributesMultibinding(AppScope::class)
class WsRouter @Inject constructor(
    private val authService: AuthenticationService,
    private val service: RoomService,
    private val messageService: MessageService
) : HttpRouter {
    override fun Routing.routes() {
        webSocket("/ws") { handleSession() }
    }

    private suspend fun WebSocketServerSession.handleSession() {
        val conn = WsConnection(this)

        val user = conn.getUser() ?: return
        val handler = ClientConnectionHandler(user, conn, service, messageService)

        handler.handleClose()
        handler.handleCommands()
    }

    // since we can't pass the authorization header in the ws connection
    // so we send first an auth command that authenticates the user
    private suspend fun ClientConnection.getUser(): UserPrincipal? {
        // get the first authentication command
        val cmd = commands()
            .filterIsInstance<WsCommand.Authenticate>()
            .first()

        val result = Either.catch { authService.jwtVerifier.verify(cmd.token) }

        return when (result) {
            is Either.Left -> {
                // on failed auth close the connection
                close()
                return null
            }
            is Either.Right -> authService.getUserFromPayload(result.value)
        }
    }
}
