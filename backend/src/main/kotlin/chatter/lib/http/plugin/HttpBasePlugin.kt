package chatter.lib.http.plugin

import io.ktor.server.application.*
import io.ktor.server.routing.*

// this class exists for solely making the new ktor plugin api compatible with our di system
//
// the new plugin api creates plugins with functions but we often need dependencies for them to work
// so this class allows for dependencies to be injected
abstract class HttpRoutePlugin<Config : Any> {
    abstract val name: String

    abstract fun createConfig(): Config
    abstract fun RouteScopedPluginBuilder<Config>.build()

    // use lazy for having to access to `name` and `build`
    val actualPlugin by lazy {
        createRouteScopedPlugin(
            name,
            ::createConfig
        ) { build() }
    }
}

fun <Config : Any> Route.install(
    plugin: HttpRoutePlugin<Config>,
    configure: Config.() -> Unit
) = install(plugin.actualPlugin, configure)

// inspired by the source for the `authenticate` function of the ktor authentication plugin
fun <Config : Any> Route.withPlugin(
    plugin: HttpRoutePlugin<Config>,
    build: Route.() -> Unit,
    configure: Config.() -> Unit,
): Route {
    val selector = object : RouteSelector() {
        override fun evaluate(context: RoutingResolveContext, segmentIndex: Int) = RouteSelectorEvaluation.Transparent

        override fun toString() = "(${plugin.name})"
    }

    val routeWithPlugin = createChild(selector)
    routeWithPlugin.install(plugin, configure)
    routeWithPlugin.build()

    return routeWithPlugin
}
