package chatter.http.routers

import chatter.domain.services.teams.MessageService
import chatter.http.TeamAuthorizationPlugin
import chatter.http.channelSlug
import chatter.http.isTeamOwnerOrParticipant
import chatter.http.teamSlug
import chatter.lib.app.AppScope
import chatter.lib.http.RouteContext
import chatter.lib.http.config.HttpRouter
import chatter.lib.http.getQueryParam
import chatter.lib.http.getQueryParamNullable
import chatter.lib.http.handle
import com.squareup.anvil.annotations.ContributesMultibinding
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import javax.inject.Inject

@ContributesMultibinding(AppScope::class)
class TeamMessagesRouter @Inject constructor(
    private val service: MessageService,
    private val authorization: TeamAuthorizationPlugin
) : HttpRouter {
    override fun Routing.routes() {
        authenticate {
            isTeamOwnerOrParticipant(authorization) {
                get("/teams/{teamSlug}/channels/{channelSlug}/messages") { getMessages() }
            }
        }
    }

    private suspend fun RouteContext.getMessages() = handle {
        val pageSize = call.getQueryParamNullable("pageSize")?.let(String::toInt)
        val timestamp = call.getQueryParam("timestamp").toLong()


        val messages = service.findByTimestamp(
            teamSlug = call.teamSlug,
            channelSlug = call.channelSlug,
            timestamp = timestamp,
            maybePageSize = pageSize
        ).bind()

        call.respond(messages)
    }
}
