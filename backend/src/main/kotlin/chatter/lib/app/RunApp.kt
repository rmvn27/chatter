package chatter.lib.app

import arrow.continuations.SuspendApp
import arrow.fx.coroutines.continuations.ResourceScope
import arrow.fx.coroutines.resourceScope
import chatter.lib.AppDispatchers
import chatter.lib.log.StdoutLogWriter
import co.touchlab.kermit.Logger

// start the application inside a coroutine scope for coroutines
// and a `ResourceScope` for proper resource management
//
// also before starting the app init the logger to have pretty logs on the terminal
fun runApp(app: suspend ResourceScope.() -> Unit) = SuspendApp(AppDispatchers.io) {
    Logger.setLogWriters(StdoutLogWriter)

    resourceScope(app)
}
