package chatter.routers

import chatter.lib.app.AppScope
import chatter.lib.http.HttpRouter
import chatter.lib.http.RouteContext
import chatter.lib.http.respond
import chatter.lib.toUUID
import chatter.services.TeamInviteService
import com.squareup.anvil.annotations.ContributesMultibinding
import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import javax.inject.Inject

@ContributesMultibinding(AppScope::class)
class TeamInviteRouter @Inject constructor(
    private val service: TeamInviteService
) : HttpRouter {
    override fun Routing.routes() {
        get("/teams/{teamId}/invites") { findMany() }
        post("/teams/{teamId}/invites") { create() }

        delete("/teams/{teamId}/invites/{inviteId}") { delete() }
    }

    private suspend fun RouteContext.findMany() {
        service.findMany(call.teamId).respond()
    }

    private suspend fun RouteContext.create() {
        service.create(call.teamId).respond()
    }

    private suspend fun RouteContext.delete() {
        service.delete(call.inviteId)
    }

    private val ApplicationCall.teamId get() = parameters.getOrFail("teamId").toUUID()

    private val ApplicationCall.inviteId get() = parameters.getOrFail("inviteId").toUUID()
}
