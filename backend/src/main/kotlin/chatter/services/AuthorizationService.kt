package chatter.services

import arrow.core.raise.either
import chatter.errors.UserIsNotTeamOwnerError
import java.util.UUID
import javax.inject.Inject

class AuthorizationService @Inject constructor(
    private val teamService: TeamService
) {
    suspend fun authorizeTeamOwner(userId: UUID, teamSlug: String) = either {
        val team = teamService.findEntity(teamSlug).bind()

        if (userId != team.ownerId) raise(UserIsNotTeamOwnerError)
    }
}
