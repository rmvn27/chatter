package chatter.lib.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asCoroutineDispatcher
import org.jetbrains.annotations.BlockingExecutor
import java.util.concurrent.Executors

// the `Virtual` dispatcher can be used to run blocking code inside coroutines
// this should be rather than the `Dispatcher.IO` dispatcher which is just
// a thread pool  of 64 threads while this dispatcher uses the new java
// virtual threads. A new thread is allocated per task and doesn't really block
// when handling blocking io in traditional java code
val Dispatchers.Virtual: @BlockingExecutor CoroutineDispatcher by lazy {
    Executors.newVirtualThreadPerTaskExecutor()
        .asCoroutineDispatcher()
}
