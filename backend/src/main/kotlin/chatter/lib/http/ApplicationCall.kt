package chatter.lib.http

import arrow.core.Either
import chatter.errors.ApplicationError
import chatter.models.UserPrincipal
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put


// if we are authenticated we certainly have a `UserPrincipal` and shouldn't get a NPE
val ApplicationCall.user get() = principal<UserPrincipal>()!!

// either respond successful response or respond with the application error
// where the status is set and the error message is provided in a json object
suspend inline fun <reified T : Any> ApplicationCall.respondWithError(data: Either<ApplicationError, T>) {
    when (data) {
        is Either.Left -> {
            response.status(data.value.status)
            respond(buildJsonObject { put("message", data.value.message) })
        }
        is Either.Right -> respond<T>(data.value)
    }
}
