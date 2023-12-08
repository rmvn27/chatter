package chatter.errors

import io.ktor.http.*
import java.util.UUID

class TeamInviteNotFoundError(id: UUID) : ApplicationError(
    "Can't find teamInvite with id '$id'",
    HttpStatusCode.NotFound
)
