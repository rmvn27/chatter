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
) : HttpRoutePlugin<IsTeamOwnerAuthorizationPlugin.Config>() {
    override val name = "isTeamOwnerAuthorization"

    override fun createConfig() = Config()
    override fun RouteScopedPluginBuilder<Config>.build() {
        // check after a successful authentication
        onWithError(AuthenticationChecked) { call ->
            val userId = call.userId
            val teamSlug = call.getParam(pluginConfig.teamSlugParam)

            authorization.authorizeTeamOwner(userId, teamSlug).bind()
        }
    }

    // provide the parameter where the teamSlug can be found
    data class Config(var teamSlugParam: String = "")
}

// the parameter of for the `teamSlug` has to be provided explicitly
// to make sure the right one is always picked up and each router
// can define it however it wants
fun Route.isTeamOwner(
    plugin: IsTeamOwnerAuthorizationPlugin,
    teamSlugParam: String,
    block: Route.() -> Unit
) = withPlugin(plugin, block) { this.teamSlugParam = teamSlugParam }
