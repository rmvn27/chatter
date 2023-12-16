package chatter.http.routers

import chatter.domain.services.teams.ChannelService
import chatter.http.EmptyJson
import chatter.http.TeamAuthorizationPlugin
import chatter.http.channelSlug
import chatter.http.isTeamOwner
import chatter.http.isTeamOwnerOrParticipant
import chatter.http.teamSlug
import chatter.lib.app.AppScope
import chatter.lib.http.RouteContext
import chatter.lib.http.config.HttpRouter
import chatter.lib.http.handle
import chatter.lib.http.status
import com.squareup.anvil.annotations.ContributesMultibinding
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import javax.inject.Inject

@ContributesMultibinding(AppScope::class)
class TeamChannelsRouter @Inject constructor(
    private val service: ChannelService,
    private val authorization: TeamAuthorizationPlugin
) : HttpRouter {
    override fun Routing.routes() {
        authenticate {
            isTeamOwnerOrParticipant(authorization) {
                get("/teams/{teamSlug}/channels") { findMany() }
            }

            isTeamOwner(authorization) {
                post("/teams/{teamSlug}/channels") { create() }
                delete("/teams/{teamSlug}/channels/{channelSlug}") { delete() }
            }
        }
    }

    private suspend fun RouteContext.findMany() = handle {
        val channels = service.findMany(call.teamSlug).bind()

        call.respond(channels)
    }

    private suspend fun RouteContext.create() = handle {
        val request = call.receive<CreateRequest>()
        val channels = service.create(
            teamSlug = call.teamSlug,
            name = request.name
        ).bind()

        call.status(HttpStatusCode.Created)
        call.respond(channels)
    }

    private suspend fun RouteContext.delete() = handle {
        service.delete(
            teamSlug = call.teamSlug,
            channelSlug = call.channelSlug
        ).bind()

        call.respond(EmptyJson)
    }

    @Serializable
    data class CreateRequest(val name: String)
}
