package chatter.routers

import arrow.core.raise.Raise
import chatter.errors.ApplicationError
import chatter.http.IsTeamOwnerAuthorizationPlugin
import chatter.http.isTeamOwner
import chatter.http.userId
import chatter.lib.app.AppScope
import chatter.lib.http.RouteContext
import chatter.lib.http.config.HttpRouter
import chatter.lib.http.getParam
import chatter.lib.http.handle
import chatter.lib.http.status
import chatter.lib.toUUID
import chatter.services.TeamInviteService
import com.squareup.anvil.annotations.ContributesMultibinding
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import javax.inject.Inject

@ContributesMultibinding(AppScope::class)
class TeamInviteRouter @Inject constructor(
    private val service: TeamInviteService,
    private val authorization: IsTeamOwnerAuthorizationPlugin
) : HttpRouter {
    override fun Routing.routes() {
        authenticate {
            // non team owners are allowed to claim invites for a team
            post("/teams/{teamSlug}/invites/{invite}/claim") { claimInvite() }

            // only team owners are allowed to see, create and delete invites
            isTeamOwner(authorization, "teamSlug") {
                get("/teams/{teamSlug}/invites") { findMany() }
                post("/teams/{teamSlug}/invites") { create() }

                delete("/teams/{teamSlug}/invites/{invite}") { delete() }
            }
        }
    }

    private suspend fun RouteContext.findMany() = handle {
        val invites = service.findMany(call.teamSlug).bind()

        call.respond(invites)
    }

    private suspend fun RouteContext.create() = handle {
        val invite = service.create(call.teamSlug)

        call.status(HttpStatusCode.Created)
        call.respond(invite)
    }

    private suspend fun RouteContext.claimInvite() = handle {
        service.claim(
            userId = call.userId,
            teamSlug = call.teamSlug,
            invite = call.invite
        ).bind()
    }

    private suspend fun RouteContext.delete() = handle {
        service.delete(call.invite)
    }

    context(Raise<ApplicationError>)
    private val ApplicationCall.teamSlug
        get() = getParam("teamSlug")

    context(Raise<ApplicationError>)
    private val ApplicationCall.invite
        get() = getParam("invite").toUUID()
}
