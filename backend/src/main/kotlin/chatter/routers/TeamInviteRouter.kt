package chatter.routers

import chatter.lib.app.AppScope
import chatter.lib.http.HttpRouter
import chatter.lib.http.RouteContext
import chatter.lib.http.respond
import chatter.lib.http.respondWithError
import chatter.lib.http.userId
import chatter.lib.toUUID
import chatter.services.TeamInviteService
import com.squareup.anvil.annotations.ContributesMultibinding
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import javax.inject.Inject

@ContributesMultibinding(AppScope::class)
class TeamInviteRouter @Inject constructor(
    private val service: TeamInviteService
) : HttpRouter {
    override fun Routing.routes() {
        authenticate {
            get("/teams/{teamSlug}/invites") { findMany() }
            post("/teams/{teamSlug}/invites") { create() }

            delete("/teams/{teamSlug}/invites/{invite}") { delete() }

            post("/teams/{teamId}/invites/{invite}/claim") { claimInvite() }
        }
    }

    private suspend fun RouteContext.findMany() {
        service.findMany(call.teamSlug).respond()
    }

    private suspend fun RouteContext.create() {
        service.create(call.teamSlug).respondWithError()
    }

    private suspend fun RouteContext.claimInvite() {
        service.claim(
            userId = call.userId,
            teamSlug = call.teamSlug,
            invite = call.invite
        )
    }

    private suspend fun RouteContext.delete() {
        service.delete(call.invite)
    }

    private val ApplicationCall.teamSlug get() = parameters.getOrFail("teamSlug")

    private val ApplicationCall.invite get() = parameters.getOrFail("invite").toUUID()
}
