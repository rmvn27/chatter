package chatter.lib.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.launch

// syntactic sugar for not having to create a `launch` block for collecting a flow
fun <T> Flow<T>.collectInScope(
    scope: CoroutineScope,
    collector: FlowCollector<T>
) = scope.launch { collect(collector) }
