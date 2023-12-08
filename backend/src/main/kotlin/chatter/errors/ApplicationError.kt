package chatter.errors

import io.ktor.http.*

// this error will be used throughout the whole application give out typed error to the client
//
// this might not be the beast approach since having proper domain errors and then converting
// them to http exceptions would be nicer, but with this we save a layer of indirection
sealed class ApplicationError(val message: String, val status: HttpStatusCode)

