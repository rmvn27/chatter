package chatter.lib.http.plugin

import arrow.core.raise.Raise
import arrow.core.raise.either
import chatter.errors.ApplicationError
import chatter.lib.http.respondError
import io.ktor.server.application.*

// provide a better interface for handling hooks that could error
//
// this only works for hooks that provide a application call
// which should be all of the basic ones like AuthenticationChecked
fun <T : Any> PluginBuilder<T>.onWithError(
    hook: Hook<suspend (ApplicationCall) -> Unit>,
    handler: suspend Raise<ApplicationError>.(ApplicationCall) -> Unit
) = on(hook) { call ->
    val result = either { handler(call) }

    // if we encounter an error respond with it
    result.onLeft { call.respondError(it) }
}
