package chatter.lib.http.plugin

import io.ktor.server.application.*
import io.ktor.server.routing.*

// this class exists for solely making the new ktor plugin api compatible with our di system
//
// the new plugin api creates plugins with functions but we often need dependencies for them to work
// so this class allows for dependencies to be injected
abstract class HttpRoutePlugin {
    abstract val name: String

    abstract fun RouteScopedPluginBuilder<Unit>.build()

    // use lazy for having to access to `name` and `build`
    val actualPlugin by lazy { createRouteScopedPlugin(name) { build() } }
}

fun Route.install(plugin: HttpRoutePlugin) = install(plugin.actualPlugin) {}

// inspired by the source for the `authenticate` function of the ktor authentication plugin
fun Route.withPlugin(plugin: HttpRoutePlugin, build: Route.() -> Unit): Route {
    val selector = object : RouteSelector() {
        override fun evaluate(context: RoutingResolveContext, segmentIndex: Int) = RouteSelectorEvaluation.Transparent

        override fun toString() = "(${plugin.name})"
    }

    val routeWithPlugin = createChild(selector)
    routeWithPlugin.install(plugin)
    routeWithPlugin.build()

    return routeWithPlugin
}
