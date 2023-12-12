package chatter.domain.services.auth

import arrow.core.raise.either
import chatter.db.TeamParticipantQueries
import chatter.db.asOptional
import chatter.domain.stores.TeamStore
import chatter.errors.UserHasNoAccessToTeamError
import chatter.errors.UserIsNotTeamOwnerError
import java.util.UUID
import javax.inject.Inject

class AuthorizationService @Inject constructor(
    private val teamStore: TeamStore,
    private val queries: TeamParticipantQueries
) {
    suspend fun authorizeTeamOwner(userId: UUID, teamSlug: String) = either {
        val team = teamStore.findBySlug(teamSlug).bind()

        if (team.ownerId != userId) raise(UserIsNotTeamOwnerError)
    }

    suspend fun authorizeTeamOwnerOrParticipant(userId: UUID, teamSlug: String) = either {
        val team = teamStore.findBySlug(teamSlug).bind()
        // the user is the owner => success
        if (team.ownerId == userId) return@either

        val maybeParticipant = queries.findParticipantByIdAndTeam(
            userId = userId,
            teamId = team.id
        ).asOptional()
        // the user is a participant => success
        if (maybeParticipant != null) return@either

        raise(UserHasNoAccessToTeamError)
    }
}
