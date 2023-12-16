package chatter.domain.services.auth

import arrow.core.raise.either
import chatter.db.TeamParticipantQueries
import chatter.db.asOptional
import chatter.domain.caches.AuthorizationCache
import chatter.domain.stores.TeamStore
import chatter.errors.UserHasNoAccessToTeamError
import chatter.errors.UserIsNotTeamOwnerError
import chatter.models.UserPrincipal
import javax.inject.Inject

class AuthorizationService @Inject constructor(
    private val teamStore: TeamStore,
    private val queries: TeamParticipantQueries,
    private val cache: AuthorizationCache
) {
    suspend fun authorizeTeamOwner(user: UserPrincipal, teamSlug: String) = either {
        val teamOwner = getTeamOwner(teamSlug).bind()
        if (teamOwner != user) raise(UserIsNotTeamOwnerError)
    }

    suspend fun authorizeTeamOwnerOrParticipant(user: UserPrincipal, teamSlug: String) = either {
        val teamOwner = getTeamOwner(teamSlug).bind()
        if (teamOwner == user) return@either

        // we only save the valid participants in the cache
        //
        // if a user has to access we will hit the db every time,
        // not optional but should improve the performance for valid users
        if (cache.checkForTeamParticipant(teamSlug, user)) return@either

        val team = teamStore.findBySlug(teamSlug).bind()
        val maybeParticipant = queries.findParticipantByIdAndTeam(
            userId = user.userId,
            teamId = team.id
        ).asOptional()
        // the user is a participant => success
        if (maybeParticipant != null) {
            cache.putTeamParticipant(teamSlug, user)
            return@either
        }

        raise(UserHasNoAccessToTeamError)
    }

    private suspend fun getTeamOwner(teamSlug: String) = either {
        // first check in cache
        val maybeOwner = cache.getTeamOwner(teamSlug)
        if (maybeOwner != null) return@either maybeOwner

        // the get from db and put in cache
        val team = teamStore.findBySlug(teamSlug).bind()
        val owner = UserPrincipal(team.ownerId)

        cache.putTeamOwner(teamSlug, owner)

        owner
    }
}
