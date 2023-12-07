package chatter.errors

import io.ktor.http.*

data object BadAuthError : ApplicationError(
    "Provided username or password is wrong!",
    HttpStatusCode.BadRequest
)
