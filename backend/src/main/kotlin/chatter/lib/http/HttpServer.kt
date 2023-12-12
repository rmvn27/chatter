package chatter.lib.http

import chatter.lib.AppDispatchers
import chatter.lib.app.AppScope
import chatter.lib.http.config.HttpApplicationConfig
import chatter.lib.http.config.HttpRouterConfiguration
import chatter.lib.log.getValue
import chatter.lib.service.StatefulService
import co.touchlab.kermit.Logger
import com.squareup.anvil.annotations.ContributesMultibinding
import com.squareup.anvil.annotations.optional.SingleIn
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import javax.inject.Inject


@SingleIn(AppScope::class)
@ContributesMultibinding(AppScope::class)
class HttpServer @Inject constructor(
    private val config: Config,
    private val applicationConfigs: Set<@JvmSuppressWildcards HttpApplicationConfig>,
    private val httpRouterConfig: HttpRouterConfiguration
) : StatefulService {
    private var server: ApplicationEngine? = null
    private val logger by Logger

    override suspend fun acquire() {
        // we just use here the base `CIO` engine
        // which should be enough for our case
        // but in a real world scenario be switched out with
        // something like `Netty`
        server = embeddedServer(
            Netty,
            host = config.host,
            port = config.port
        ) {
            applicationConfigs.forEach {
                with(it) { this@embeddedServer.configure() }
            }

            // configure the routing only after all other plugins were configured
            with(httpRouterConfig) { this@embeddedServer.configure() }
        }
    }

    override suspend fun start(): Unit = withContext(AppDispatchers.io) {
        logger.i { "Listening for connections on: ${config.host}:${config.port}" }
        server?.start(wait = false)
    }

    override suspend fun release() = withContext(AppDispatchers.io) {
        logger.i { "Shutting down" }
        // use a longer grace period
        server?.stop(
            gracePeriodMillis = 5_000,
            timeoutMillis = 5_000
        )

        server = null
    }

    @Serializable
    data class Config(
        val host: String = "0.0.0.0",
        val port: Int = 8080
    )
}
