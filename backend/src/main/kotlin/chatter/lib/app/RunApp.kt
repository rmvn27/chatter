package chatter.lib.app

import arrow.continuations.SuspendApp
import arrow.fx.coroutines.continuations.ResourceScope
import arrow.fx.coroutines.resourceScope
import chatter.lib.coroutines.Virtual
import chatter.lib.log.StdoutLogWriter
import chatter.lib.log.setSeverityFromEnv
import co.touchlab.kermit.Logger
import kotlinx.coroutines.Dispatchers

// start the application inside a coroutine scope for coroutines
// and a `ResourceScope` for proper resource management
//
// also before starting the app init the logger to have pretty logs on the terminal
fun runApp(app: suspend ResourceScope.() -> Unit) = SuspendApp(Dispatchers.Virtual) {
    Logger.setLogWriters(StdoutLogWriter)
    Logger.setSeverityFromEnv()

    resourceScope(app)
}
