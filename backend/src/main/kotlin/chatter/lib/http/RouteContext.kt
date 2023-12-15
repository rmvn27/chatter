package chatter.lib.http

import arrow.core.raise.Raise
import arrow.core.raise.either
import chatter.errors.ApplicationError
import chatter.errors.ParameterNotFoundError
import chatter.errors.QueryParameterNotFoundError
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.util.pipeline.*
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

typealias RouteContext = PipelineContext<Unit, ApplicationCall>

// improve the usage with working with application errors in the route handlers
//
// for this the lambda can be an extension function of `Raise<ApplicationError>` which
// allows for short circuiting errors if they might happen. then the route will just return an error
//
// not that we don't implicitly respond of the successful value of the block. to make
// sure that the we don't respond with the last value accidentally. instead the caller
// as to do it explicitly
suspend fun RouteContext.handle(block: suspend Raise<ApplicationError>.() -> Unit) {
    val result = either { block() }

    // we don't care about the successful value and only care if an error gets raised
    result.onLeft { call.respondError(it) }
}


// we set the proper status code and put the message in a json object
suspend fun ApplicationCall.respondError(error: ApplicationError) {
    response.status(error.status)
    respond(buildJsonObject { put("message", error.message) })
}

fun ApplicationCall.status(statusCode: HttpStatusCode) = response.status(statusCode)

// get the parameter out of the request route
// this should usually not fail but there is chance we made a type
// and then we want to return a internal error
context(Raise<ApplicationError>)
fun ApplicationCall.getParam(name: String) = parameters[name] ?: raise(ParameterNotFoundError(name))

// get the parameter out of the request route
// this should usually not fail but there is chance we made a type
// and then we want to return a internal error
context(Raise<ApplicationError>)
fun ApplicationCall.getQueryParam(name: String) =
    request.queryParameters[name] ?: raise(QueryParameterNotFoundError(name))

fun ApplicationCall.getQueryParamNullable(name: String) = request.queryParameters[name]
