package chatter.lib

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

// safely store data in a thread sync manner
data class Locked<T>(
    private var lockData: T,
    private val mutex: Mutex = Mutex()
) {
    suspend fun get() = mutex.withLock { lockData }

    suspend fun <R : T> update(action: suspend (T) -> R) = mutex.withLock {
        action(lockData).also { lockData = it }
    }

    suspend fun <R> withLock(action: suspend (T) -> R): R = mutex.withLock {
        action(lockData)
    }
}

suspend fun <T> Locked<T?>.getOrPut(
    block: suspend () -> T
) = update { it ?: block() }
