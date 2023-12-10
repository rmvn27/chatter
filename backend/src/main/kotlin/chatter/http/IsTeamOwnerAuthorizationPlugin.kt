package chatter.http

import chatter.lib.app.AppScope
import chatter.lib.http.getParam
import chatter.lib.http.plugin.HttpRoutePlugin
import chatter.lib.http.plugin.onWithError
import chatter.lib.http.plugin.withPlugin
import chatter.services.AuthorizationService
import com.squareup.anvil.annotations.optional.SingleIn
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import javax.inject.Inject

// check whether the logged in user is the owner of the accessed team
//
// should be only installed on routes that define a `{teamSlug}` parameter
@SingleIn(AppScope::class)
class IsTeamOwnerAuthorizationPlugin @Inject constructor(
    private val authorization: AuthorizationService
) : HttpRoutePlugin() {
    override val name = "isTeamOwnerAuthorization"

    override fun RouteScopedPluginBuilder<Unit>.build() {
        // check after a successful authentication
        onWithError(AuthenticationChecked) { call ->
            val userId = call.userId
            val teamSlug = call.getParam("teamSlug")

            authorization.authorizeTeamOwner(userId, teamSlug).bind()
        }
    }
}

fun Route.isTeamOwner(plugin: IsTeamOwnerAuthorizationPlugin, block: Route.() -> Unit) = withPlugin(plugin, block)
