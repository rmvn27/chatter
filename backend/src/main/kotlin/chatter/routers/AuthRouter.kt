package chatter.routers

import chatter.lib.app.AppScope
import chatter.lib.http.HttpRouter
import chatter.lib.http.RouteContext
import chatter.lib.http.respondWithError
import chatter.lib.serialization.UUIDSerializer
import chatter.services.AuthService
import com.squareup.anvil.annotations.ContributesMultibinding
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import java.util.UUID
import javax.inject.Inject

@ContributesMultibinding(AppScope::class)
class AuthRouter @Inject constructor(
    private val authService: AuthService
) : HttpRouter {
    override fun Routing.routes() {
        post("/auth/register") { register() }
        post("/auth/login") { login() }

        post("/auth/tokens") { regenerateTokens() }

        post("/auth/logout") { logout() }
    }

    private suspend fun RouteContext.register() {
        val request = call.receive<AuthRequest>()
        authService.register(
            username = request.username,
            password = request.password
        ).respondWithError()
    }

    private suspend fun RouteContext.login() {
        val request = call.receive<AuthRequest>()
        authService.login(
            username = request.username,
            password = request.password
        ).respondWithError()
    }

    private suspend fun RouteContext.regenerateTokens() {
        val request = call.receive<RegenerateTokensRequest>()

        authService.regenerateTokens(request.refreshToken)
            .respondWithError()
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
