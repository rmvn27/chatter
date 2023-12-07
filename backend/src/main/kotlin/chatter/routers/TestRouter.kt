package chatter.routers

import chatter.lib.app.AppScope
import chatter.lib.http.HttpRouter
import chatter.lib.http.user
import com.squareup.anvil.annotations.ContributesMultibinding
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import javax.inject.Inject

@ContributesMultibinding(AppScope::class)
class TestRouter @Inject constructor() : HttpRouter {
    override fun Routing.routes() {
        authenticate {
            get("/") {
                call.respond(call.user)
            }
        }
    }
}
