package chatter.http.routers

import arrow.core.Either
import chatter.domain.services.auth.AuthenticationService
import chatter.domain.services.live.TeamRoomService
import chatter.http.WsConnection
import chatter.lib.app.AppScope
import chatter.lib.http.config.HttpRouter
import chatter.models.WsCommand
import com.squareup.anvil.annotations.ContributesMultibinding
import io.ktor.serialization.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import java.util.UUID
import javax.inject.Inject

@ContributesMultibinding(AppScope::class)
class WsRouter @Inject constructor(
    private val authService: AuthenticationService,
    private val service: TeamRoomService
) : HttpRouter {
    private val verifier = authService.buildVerifier()

    override fun Routing.routes() {
        webSocket("/ws") { handleSession() }
    }

    private suspend fun WebSocketServerSession.handleSession() {
        val userId = getUserId() ?: return

        val conn = WsConnection(userId = userId, session = this)
        service.handle(conn)
    }

    // since we can't pass the authorization header in the we connection
    // we send a first auth command that authenticates the user
    private suspend fun WebSocketServerSession.getUserId(): UUID? {
        // get the first authentication command
        val cmd = incoming.receiveAsFlow()
            .map { converter!!.deserialize<WsCommand>(it) }
            .filterIsInstance<WsCommand.Authenticate>()
            .first()

        val result = Either.catch { verifier.verify(cmd.token) }

        return when (result) {
            is Either.Left -> {
                // on failed auth close the connection
                close()
                return null
            }
            is Either.Right -> authService.createPrincipal(result.value).userId
        }
    }
}
