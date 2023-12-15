package chatter.lib

import chatter.lib.app.AppScope
import chatter.lib.log.getValue
import chatter.lib.serialization.JsonParsers
import chatter.lib.service.StatefulService
import co.touchlab.kermit.Logger
import com.squareup.anvil.annotations.ContributesMultibinding
import com.squareup.anvil.annotations.optional.SingleIn
import io.nats.client.Connection
import io.nats.client.Dispatcher
import io.nats.client.Message
import io.nats.client.Nats
import io.nats.client.Options
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.future.await
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import java.time.Duration
import javax.inject.Inject

@SingleIn(AppScope::class)
@ContributesMultibinding(AppScope::class)
class NatsService @Inject constructor(
    private val config: Config
) : StatefulService {
    val json = JsonParsers.strict

    private val logger by Logger

    lateinit var connection: Connection
        private set

    val dispatchers = mutableSetOf<Dispatcher>()

    // convert the callback api to a flow, that emits liveMessages
    inline fun <reified T> messages(subject: String): Flow<Message<T>> {
        val msgFlow = callbackFlow {
            val dispatcher = connection.createDispatcher {
                trySend(it)
            }.also(dispatchers::add)

            dispatcher.subscribe(subject)

            awaitClose { closeDispatcher(dispatcher) }
        }

        return msgFlow.map {
            Message(
                it.subject,
                json.decodeFromString<T>(it.data.toString(Charsets.UTF_8))
            )
        }
    }

    inline fun <reified T> publish(subject: String, data: T) {
        connection.publish(subject, json.encodeToString(data).toByteArray(Charsets.UTF_8))
    }

    override suspend fun acquire() = withContext(AppDispatchers.io) {
        logger.i { "Connecting" }
        val options = Options.Builder().apply {
            server(config.server)
            maxReconnects(config.reconnects)
        }.build()

        connection = Nats.connect(options)
    }

    override suspend fun release() {
        logger.i { "Closing connections" }
        // if we have still any dispatchers left, close them
        dispatchers.forEach { closeDispatcher(it) }

        connection.drain(Duration.ZERO).await()
    }

    // close the dispatcher and remove it from the kept ones
    fun closeDispatcher(dispatcher: Dispatcher) {
        connection.closeDispatcher(dispatcher)
        dispatchers.remove(dispatcher)
    }

    data class Message<T>(
        val subject: String,
        val data: T
    )

    @Serializable
    data class Config(
        val server: String = "nats://localhost:4222",
        val reconnects: Int = -1
    )
}
