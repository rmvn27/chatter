package chatter.lib

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

// these `CoroutineDispatchers` are used when blocking code is used inside coroutines
// when the bridging between normal java code and kotlin coroutines is needed.
// the dispatchers manage where the coroutine code is run
//
// we have currently two dispatchers for that:
// - a simple io dispatcher that is just the default `Dispatchers.IO` and is a thread pool of 64 threads
// - a db dispatcher that also has 64 threads but here just for the db connections.
//   it is created from the io dispatcher which is elastic and has theoretically a unlimited amount of thread
//   in the thread  pool. it itself can only use 64 of them but can give out more when a separate dispatcher is created
object AppDispatchers {
    val io: CoroutineDispatcher by lazy { Dispatchers.IO }

    // Separate dispatcher for db actions
    val db: CoroutineDispatcher by lazy { io.limitedParallelism(64) }
}
