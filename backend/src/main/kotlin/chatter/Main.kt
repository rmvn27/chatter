package chatter

import io.ktor.server.application.*
import io.ktor.server.cio.*
import io.ktor.server.engine.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main() {
    val server = embeddedServer(CIO, port = 8081, host = "0.0.0.0", module = Application::mainModule)

    server.start(wait = true)
}

fun Application.mainModule() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
    }
}
