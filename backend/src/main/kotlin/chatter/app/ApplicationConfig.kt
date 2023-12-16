package chatter.app

import chatter.db.DatabaseService
import chatter.domain.services.auth.AuthenticationService
import chatter.lib.NatsService
import chatter.lib.cache.RedisService
import chatter.lib.http.HttpServer
import chatter.lib.serialization.JsonParsers
import kotlinx.serialization.Serializable
import java.io.File

// hold all of the configuration that is needed for all of the services in the application
@Serializable
data class ApplicationConfig(
    val db: DatabaseService.Config,
    val auth: AuthenticationService.Config,
    val http: HttpServer.Config = HttpServer.Config(),
    val redis: RedisService.Config = RedisService.Config(),
    val nats: NatsService.Config = NatsService.Config()
) {
    companion object {
        // read the application config from a json file
        // this will also include secrets so don't put it in version control!
        fun read(): ApplicationConfig {
            val configLocation = System.getenv("CHATTER_CONFIG_LOCATION") ?: "./config.json"
            val content = File(configLocation).readText()

            return JsonParsers.nonStrict.decodeFromString<ApplicationConfig>(content)
        }
    }
}
