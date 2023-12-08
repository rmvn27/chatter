package chatter.routers

import chatter.lib.app.AppScope
import chatter.lib.http.HttpRouter
import chatter.lib.http.RouteContext
import chatter.lib.http.respond
import chatter.lib.http.respondWithError
import chatter.lib.http.userId
import chatter.services.TeamService
import com.squareup.anvil.annotations.ContributesMultibinding
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import kotlinx.serialization.Serializable
import javax.inject.Inject

@ContributesMultibinding(AppScope::class)
class TeamsRouter @Inject constructor(
    private val service: TeamService
) : HttpRouter {
    override fun Routing.routes() {
        authenticate {
            get("/teams") { findMany() }
            get("/teams/{teamSlug}") { findById() }
            post("/teams") { create() }
            patch("/teams/{teamSlug}") { update() }
            delete("/teams/{teamSlug}") { delete() }
        }
    }

    private suspend fun RouteContext.findMany() {
        service.findForUser(call.userId).respond()
    }

    private suspend fun RouteContext.findById() {
        service.findBySlug(
            userId = call.userId,
            teamSlug = call.teamSlug
        ).respondWithError()
    }

    private suspend fun RouteContext.create() {
        val name = call.receive<CreateRequest>().name
        service.create(name, call.userId).respond()
    }

    private suspend fun RouteContext.update() {
        val body = call.receive<UpdateRequest>()
        service.update(
            userId = call.userId,
            teamSlug = call.teamSlug,
            name = body.name
        ).respondWithError()
    }

    private suspend fun RouteContext.delete() {
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

    private val ApplicationCall.teamSlug get() = parameters.getOrFail("teamSlug")
}
