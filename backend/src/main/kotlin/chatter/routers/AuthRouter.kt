package chatter.routers

import chatter.lib.app.AppScope
import chatter.lib.http.HttpRouter
import chatter.lib.http.RouteContext
import chatter.lib.http.handle
import chatter.lib.http.status
import chatter.lib.serialization.UUIDSerializer
import chatter.services.AuthenticationService
import com.squareup.anvil.annotations.ContributesMultibinding
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import java.util.UUID
import javax.inject.Inject

@ContributesMultibinding(AppScope::class)
class AuthRouter @Inject constructor(
    private val authService: AuthenticationService
) : HttpRouter {
    override fun Routing.routes() {
        post("/auth/register") { register() }
        post("/auth/login") { login() }

        post("/auth/tokens") { regenerateTokens() }

        post("/auth/logout") { logout() }
    }

    private suspend fun RouteContext.register() = handle {
        val request = call.receive<AuthRequest>()

        val tokens = authService.register(
            username = request.username,
            password = request.password
        ).bind()

        // we created a new user set this to created
        call.status(HttpStatusCode.Created)
        call.respond(tokens)
    }

    private suspend fun RouteContext.login() = handle {
        val request = call.receive<AuthRequest>()
        val tokens = authService.login(
            username = request.username,
            password = request.password
        ).bind()
        call.respond(tokens)
    }

    private suspend fun RouteContext.regenerateTokens() = handle {
        val request = call.receive<RegenerateTokensRequest>()

        val tokens = authService.regenerateTokens(request.refreshToken).bind()
        call.respond(tokens)
    }

    private suspend fun RouteContext.logout() {
        val request = call.receive<LogoutRequest>()

        authService.logout(request.refreshToken)
    }

    @Serializable
    data class AuthRequest(
        val username: String,
        val password: String
    )

    @Serializable
    data class RegenerateTokensRequest(
        @Serializable(with = UUIDSerializer::class)
        val refreshToken: UUID
    )

    @Serializable
    data class LogoutRequest(
        @Serializable(with = UUIDSerializer::class)
        val refreshToken: UUID
    )
}
