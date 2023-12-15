package chatter.lib.http.config

import chatter.lib.app.AppScope
import chatter.lib.http.status
import chatter.lib.serialization.JsonParsers
import co.touchlab.kermit.Logger
import com.squareup.anvil.annotations.ContributesMultibinding
import io.ktor.http.*
import io.ktor.serialization.kotlinx.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.response.*
import io.ktor.server.websocket.*
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

// base config for the `HttpServer`
//
// - configure json serialization
// - configure cors
@ContributesMultibinding(AppScope::class)
object HttpBaseApplicationConfig : HttpApplicationConfig {
    private val logger = Logger.withTag("HttpServer")

    override fun Application.configure() {
        install(ContentNegotiation) { json(JsonParsers.strict) }
        install(WebSockets) {
            // allow for handling json liveMessages
            contentConverter = KotlinxWebsocketSerializationConverter(JsonParsers.strict)
        }

        // for now just allow everything
        install(CORS) {
            // every host
            anyHost()
            // every method (get, post, head are already allowed)
            allowMethod(HttpMethod.Options)
            allowMethod(HttpMethod.Put)
            allowMethod(HttpMethod.Patch)
            allowMethod(HttpMethod.Delete)
            // additional headers
            allowHeader(HttpHeaders.ContentType)
            allowHeader(HttpHeaders.Authorization)
        }

        install(StatusPages) {
            exception<Throwable> { call, cause ->
                logger.e(cause) { "Encountered error during the request" }

                call.status(HttpStatusCode.InternalServerError)
                call.respond(buildJsonObject { put("message", "Couldn't handle the request!") })
            }

            // add a proper json body, the frontend expects it
            status(HttpStatusCode.Unauthorized) { call, status ->
                call.status(status)
                call.respond(buildJsonObject { put("message", "User is not authorized!") })
            }
        }
    }
}
