package chatter.errors

import io.ktor.http.*

class TeamNotFoundError(slug: String) : ApplicationError(
    "Can't find team with slug '$slug'",
    HttpStatusCode.NotFound
)
