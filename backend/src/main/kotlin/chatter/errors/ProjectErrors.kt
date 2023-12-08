package chatter.errors

import java.util.UUID

class ProjectNotFoundError(id: UUID) : NotFoundError("Project", id)
