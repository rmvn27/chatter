package chatter.domain.services.auth

import arrow.core.raise.either
import chatter.domain.services.TeamService
import chatter.errors.UserIsNotTeamOwnerError
import java.util.UUID
import javax.inject.Inject

class AuthorizationService @Inject constructor(
    private val teamService: TeamService
) {
    suspend fun authorizeTeamOwner(userId: UUID, teamSlug: String) = either {
        val team = teamService.findBySlug(teamSlug, userId).bind()

        if (!team.isOwner) raise(UserIsNotTeamOwnerError)
    }
}
