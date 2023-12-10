package chatter.errors

import io.ktor.http.*

data object InvalidTeamInviteError : ApplicationError(
    "Team invite is invalid",
    HttpStatusCode.BadRequest
)
