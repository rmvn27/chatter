package chatter.errors

import io.ktor.http.*

data class UserAlreadyExistsError(val username: String) : ApplicationError(
    "the username '$username' is already taken!",
    HttpStatusCode.BadRequest
)
