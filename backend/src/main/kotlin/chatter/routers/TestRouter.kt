package chatter.routers

import chatter.lib.app.AppScope
import chatter.lib.http.HttpRouter
import com.squareup.anvil.annotations.ContributesMultibinding
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import javax.inject.Inject

@ContributesMultibinding(AppScope::class)
class TestRouter @Inject constructor() : HttpRouter {
    override fun Routing.routes() {
        get("/") {
            call.respondText { "Hello World!" }
        }
    }
}
