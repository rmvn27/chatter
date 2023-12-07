package chatter.lib.http

import chatter.lib.app.AppScope
import chatter.services.AuthService
import com.squareup.anvil.annotations.ContributesMultibinding
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import javax.inject.Inject

// install the jwt authentication that is backed up by the auth service
@ContributesMultibinding(AppScope::class)
class HttpAuthConfig @Inject constructor(
    private val authService: AuthService
) : HttpApplicationConfig {
    override fun Application.configure() {
        install(Authentication) {
            jwt { authService.createJwtAuthentication() }
        }
    }
}
