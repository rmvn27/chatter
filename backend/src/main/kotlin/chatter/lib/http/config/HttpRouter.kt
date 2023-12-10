package chatter.lib.http.config

import io.ktor.server.application.*
import io.ktor.server.routing.*
import javax.inject.Inject

// base for all routers for the application.
// here we also use interfaces for enable a proper automatic dependency injection by dagger.
//
// the router can configure its routes in the `routes` function which acts under
// the `Routing` context and can so register its own routes
interface HttpRouter {
    // use this as a extension method to have all all routing method available implicitly
    fun Routing.routes()
}

class HttpRouterConfiguration @Inject constructor(
    private val routers: Set<@JvmSuppressWildcards HttpRouter>
) : HttpApplicationConfig {
    override fun Application.configure() {
        routing {
            routers.forEach {
                with(it) { this@routing.routes() }
            }
        }
    }
}
