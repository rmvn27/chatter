package chatter.routers

import arrow.core.raise.Raise
import chatter.errors.ApplicationError
import chatter.http.IsTeamOwnerAuthorizationPlugin
import chatter.http.isTeamOwner
import chatter.http.userId
import chatter.lib.app.AppScope
import chatter.lib.http.HttpRouter
import chatter.lib.http.RouteContext
import chatter.lib.http.getParam
import chatter.lib.http.handle
import chatter.lib.http.status
import chatter.services.TeamService
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
class TeamsRouter @Inject constructor(
    private val service: TeamService,
    private val authorization: IsTeamOwnerAuthorizationPlugin
) : HttpRouter {
    override fun Routing.routes() {
        authenticate {
            get("/teams") { findMany() }
            get("/teams/{teamSlug}") { findById() }
            post("/teams") { create() }

            // only team owners can update and delete their teams
            isTeamOwner(authorization, "teamSlug") {
                patch("/teams/{teamSlug}") { update() }
                delete("/teams/{teamSlug}") { delete() }
            }
        }
    }

    private suspend fun RouteContext.findMany() {
        val teams = service.findForUser(call.userId)
        call.respond(teams)
    }

    private suspend fun RouteContext.findById() = handle {
        val team = service.findBySlug(
            userId = call.userId,
            teamSlug = call.teamSlug
        ).bind()

        call.respond(team)
    }

    private suspend fun RouteContext.create() {
        val name = call.receive<CreateRequest>().name
        val team = service.create(name, call.userId)

        call.status(HttpStatusCode.Created)
        call.respond(team)
    }

    private suspend fun RouteContext.update() = handle {
        val body = call.receive<UpdateRequest>()
        val newTeam = service.update(
            userId = call.userId,
            teamSlug = call.teamSlug,
            name = body.name
        ).bind()

        call.respond(newTeam)
    }

    private suspend fun RouteContext.delete() = handle {
        service.delete(call.teamSlug)
    }

    @Serializable
    data class CreateRequest(
        val name: String,
    )

    @Serializable
    data class UpdateRequest(
        val name: String? = null
    )

    context(Raise<ApplicationError>)
    private val ApplicationCall.teamSlug
        get() = getParam("teamSlug")
}
