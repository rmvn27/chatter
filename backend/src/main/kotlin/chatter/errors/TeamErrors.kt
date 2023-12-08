package chatter.errors

import io.ktor.http.*

class ProjectNotFoundError(slug: String) : ApplicationError(
    "Can't find team with slug '$slug'",
    HttpStatusCode.NotFound
)
