package chatter.lib.http

import chatter.lib.app.AppScope
import chatter.lib.serialization.JsonParsers
import com.squareup.anvil.annotations.ContributesMultibinding
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*

// base config for the `HttpServer`
//
// - configure json serialization
// - configure cors
@ContributesMultibinding(AppScope::class)
object HttpBaseApplicationConfig : HttpApplicationConfig {
    override fun Application.configure() {
        install(ContentNegotiation) { json(JsonParsers.strict) }

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
    }
}
