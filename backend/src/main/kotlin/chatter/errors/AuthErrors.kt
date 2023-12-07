package chatter.errors

import io.ktor.http.*

data object BadAuthError : ApplicationError(
    "Provided username or password is wrong!",
    HttpStatusCode.BadRequest
)

data object BadRefreshToken : ApplicationError("The provided refresh token is invalid!", HttpStatusCode.Unauthorized)
