package chatter.http.routers

import arrow.core.raise.Raise
import chatter.domain.services.teams.ParticipantService
import chatter.errors.ApplicationError
import chatter.http.EmptyJson
import chatter.http.TeamAuthorizationPlugin
import chatter.http.isTeamOwner
import chatter.http.isTeamOwnerOrParticipant
import chatter.http.teamSlug
import chatter.lib.app.AppScope
import chatter.lib.http.RouteContext
import chatter.lib.http.config.HttpRouter
import chatter.lib.http.getParam
import chatter.lib.http.handle
import chatter.lib.toUUID
import com.squareup.anvil.annotations.ContributesMultibinding
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import javax.inject.Inject

@ContributesMultibinding(AppScope::class)
class TeamParticipantsRouter @Inject constructor(
    private val service: ParticipantService,
    private val authorization: TeamAuthorizationPlugin
) : HttpRouter {
    override fun Routing.routes() {
        authenticate {
            isTeamOwnerOrParticipant(authorization) {
                get("/teams/{teamSlug}/participants") { findMany() }
            }

            // only the teamOwner can directly delete participants
            // the users themselves can leave through the teams router
            isTeamOwner(authorization) {
                delete("/teams/{teamSlug}/participants/{participantId}") { delete() }
            }
        }
    }

    private suspend fun RouteContext.findMany() = handle {
        val users = service.findMany(call.teamSlug).bind()

        call.respond(users)
    }

    private suspend fun RouteContext.delete() = handle {
        service.delete(
            teamSlug = call.teamSlug,
            userId = call.participantId
        ).bind()
        call.respond(EmptyJson)
    }

    context(Raise<ApplicationError>)
    private val ApplicationCall.participantId
        get() = getParam("participantId").toUUID()
}
