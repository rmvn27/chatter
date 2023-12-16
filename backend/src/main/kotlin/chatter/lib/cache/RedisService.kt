package chatter.lib.cache

import chatter.lib.app.AppScope
import chatter.lib.coroutines.Virtual
import chatter.lib.log.getValue
import chatter.lib.service.StatefulService
import co.touchlab.kermit.Logger
import com.squareup.anvil.annotations.ContributesMultibinding
import com.squareup.anvil.annotations.optional.SingleIn
import io.lettuce.core.RedisClient
import io.lettuce.core.api.StatefulRedisConnection
import io.lettuce.core.api.async.RedisAsyncCommands
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.future.await
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import javax.inject.Inject

// connect to a redis instance and expose the async variant of
// it's commands. While the commands use java's `CompletableFuture`
// in kotlin we can just await them
@SingleIn(AppScope::class)
@ContributesMultibinding(AppScope::class)
class RedisService @Inject constructor(
    config: Config
) : StatefulService {
    private val logger by Logger
    private val client = RedisClient.create(config.url)

    private lateinit var connection: StatefulRedisConnection<String, String>

    lateinit var commands: RedisAsyncCommands<String, String>
        private set


    override suspend fun acquire() = withContext(Dispatchers.Virtual) {
        logger.i { "Connecting" }

        connection = client.connect()
        commands = connection.async()
    }

    override suspend fun release() {
        logger.i { "Shutting Down" }
        connection.closeAsync().await()
    }

    @Serializable
    data class Config(
        val url: String = "redis://127.0.0.1:6379"
    )
}
