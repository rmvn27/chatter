package chatter.errors

import io.ktor.http.*

class TeamNotFoundError(slug: String) : ApplicationError(
    "Can't find team with slug '$slug'",
    HttpStatusCode.NotFound
)

data object InvalidTeamInviteError : ApplicationError(
    "Team invite is invalid",
    HttpStatusCode.BadRequest
)

class ChannelNotFoundError(slug: String) : ApplicationError(
    "Can't find channel with slug '$slug'",
    HttpStatusCode.NotFound
)
