package chatter.errors

import io.ktor.http.*

// this error will be used throughout the whole application give out typed error to the client
//
// this might not be the beast approach since having proper domain errors and then converting
// them to http exceptions would be nicer, but with this we save a layer of indirection
sealed class ApplicationError(val message: String, val status: HttpStatusCode)

sealed class InternalError : ApplicationError(
    "Can't handle currently the request!",
    HttpStatusCode.InternalServerError
)

// this error should not happen - but we could log to see if we made a typo somewhere accidentally
data class ParameterNotFoundError(val parameter: String) : InternalError()
data class QueryParameterNotFoundError(val queryParameter: String) : InternalError()
