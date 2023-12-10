package chatter.http

import chatter.lib.app.AppScope
import chatter.lib.http.config.HttpApplicationConfig
import chatter.models.UserPrincipal
import chatter.domain.services.auth.AuthenticationService
import com.squareup.anvil.annotations.ContributesMultibinding
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import javax.inject.Inject

// install the jwt authentication that is backed up by the auth service
@ContributesMultibinding(AppScope::class)
class HttpAuthenticationConfig @Inject constructor(
    private val authService: AuthenticationService
) : HttpApplicationConfig {
    override fun Application.configure() {
        install(Authentication) {
            jwt { authService.createJwtAuthentication() }
        }
    }
}

// if we are authenticated we certainly have a `UserPrincipal` and shouldn't get a NPE
val ApplicationCall.userId get() = principal<UserPrincipal>()!!.userId
