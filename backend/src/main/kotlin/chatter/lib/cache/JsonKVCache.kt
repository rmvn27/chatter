package chatter.lib.cache

import chatter.lib.serialization.JsonParsers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.future.await
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.KSerializer

abstract class JsonKVCache<V>(private val redis: RedisService) {
    abstract val prefix: String
    abstract val serializer: KSerializer<V>

    private val mutex = Mutex()

    suspend fun get(key: String): V? = redis.commands
        .get(buildKey(key))
        .await()
        ?.let(::deserializeValue)

    suspend fun put(key: String, value: V) {
        redis.commands
            .set(buildKey(key), serializeValue(value))
            .await()
    }

    // make sur the creation step is only done one time
    suspend fun getOrPut(key: String, creator: suspend () -> V): V {
        val maybeValue = get(key)
        if (maybeValue != null) return maybeValue

        return mutex.withLock {
            // check after we acquired the lock
            val innerMaybeValue = get(key)
            if (innerMaybeValue != null) return@withLock innerMaybeValue

            creator().also { put(key, it) }
        }
    }

    suspend fun delete(key: String) {
        redis.commands.del(buildKey(key)).await()
    }

    suspend fun deleteAll() = coroutineScope {
        val keys = redis.commands.keys("$prefix:*").await()

        keys.map { async { redis.commands.del(it).await() } }.awaitAll()
    }

    private fun buildKey(key: String) = "$prefix:$key"
    private fun serializeValue(value: V) = JsonParsers.strict.encodeToString(serializer, value)
    private fun deserializeValue(value: String) = JsonParsers.strict.decodeFromString(serializer, value)
}
