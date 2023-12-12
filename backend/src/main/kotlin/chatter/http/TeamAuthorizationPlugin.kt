package chatter.http

import chatter.domain.services.auth.AuthorizationService
import chatter.lib.app.AppScope
import chatter.lib.http.getParam
import chatter.lib.http.plugin.HttpRoutePlugin
import chatter.lib.http.plugin.onWithError
import chatter.lib.http.plugin.withPlugin
import com.squareup.anvil.annotations.optional.SingleIn
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import javax.inject.Inject

// check whether the logged in user has access to a team. It can be further constrained
// whether an action can be done by both owners and participants or just participants
//
// should be only installed on routes that define a `{teamSlug}` parameter
@SingleIn(AppScope::class)
class TeamAuthorizationPlugin @Inject constructor(
    private val authorization: AuthorizationService
) : HttpRoutePlugin<TeamAuthorizationPlugin.Config>() {
    override val name = "isTeamOwnerAuthorization"

    override fun createConfig() = Config()
    override fun RouteScopedPluginBuilder<Config>.build() {
        when (pluginConfig.rule) {
            Rule.IsTeamOwner -> isTeamOwner()
            Rule.IsTeamOwnerOrParticipant -> isTeamOwnerOrParticipant()
        }
    }

    private fun RouteScopedPluginBuilder<Config>.isTeamOwner() {
        // check after a successful authentication
        onWithError(AuthenticationChecked) { call ->
            val userId = call.userId
            val teamSlug = call.getParam(pluginConfig.teamSlugParam)

            authorization.authorizeTeamOwner(userId, teamSlug).bind()
        }
    }

    private fun RouteScopedPluginBuilder<Config>.isTeamOwnerOrParticipant() {
        // check after a successful authentication
        onWithError(AuthenticationChecked) { call ->
            val userId = call.userId
            val teamSlug = call.getParam(pluginConfig.teamSlugParam)

            authorization.authorizeTeamOwnerOrParticipant(userId, teamSlug).bind()
        }
    }


    // provide the parameter where the teamSlug can be found
    data class Config(var teamSlugParam: String = "", var rule: Rule = Rule.IsTeamOwner)

    enum class Rule {
        IsTeamOwner,
        IsTeamOwnerOrParticipant
    }
}

// the parameter of for the `teamSlug` has to be provided explicitly
// to make sure the right one is always picked up and each router
// can define it however it wants
fun Route.isTeamOwner(
    plugin: TeamAuthorizationPlugin,
    teamSlugParam: String,
    block: Route.() -> Unit
) = withPlugin(plugin, block) {
    this.teamSlugParam = teamSlugParam
    this.rule = TeamAuthorizationPlugin.Rule.IsTeamOwner
}

fun Route.isTeamOwnerOrParticipant(
    plugin: TeamAuthorizationPlugin,
    teamSlugParam: String,
    block: Route.() -> Unit
) = withPlugin(plugin, block) {
    this.teamSlugParam = teamSlugParam
    this.rule = TeamAuthorizationPlugin.Rule.IsTeamOwnerOrParticipant
}
