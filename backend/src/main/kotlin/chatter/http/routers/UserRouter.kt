package chatter.http.routers

import chatter.domain.services.UserService
import chatter.http.EmptyJson
import chatter.http.userId
import chatter.lib.app.AppScope
import chatter.lib.http.RouteContext
import chatter.lib.http.config.HttpRouter
import com.squareup.anvil.annotations.ContributesMultibinding
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable
import javax.inject.Inject

@ContributesMultibinding(AppScope::class)
class UserRouter @Inject constructor(
    private val service: UserService
) : HttpRouter {
    override fun Routing.routes() {
        authenticate {
            patch("/user") { update() }
            delete("/user") { delete() }
        }
    }

    private suspend fun RouteContext.update() {
        val request = call.receive<UpdateRequest>()

        service.update(
            userId = call.userId,
            password = request.password,
            name = request.name
        )

        call.respond(EmptyJson)
    }

    private suspend fun RouteContext.delete() {
        service.delete(call.userId)

        call.respond(EmptyJson)
    }

    @Serializable
    data class UpdateRequest(
        val password: String? = null,
        val name: String? = null
    )
}
