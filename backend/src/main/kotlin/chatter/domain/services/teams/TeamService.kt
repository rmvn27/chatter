package chatter.domain.services.teams

import arrow.core.raise.either
import chatter.db.display
import chatter.domain.caches.AuthorizationCache
import chatter.domain.stores.TeamStore
import chatter.lib.log.getValue
import chatter.models.Team
import chatter.models.UserPrincipal
import chatter.models.toDomain
import co.touchlab.kermit.Logger
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.util.UUID
import javax.inject.Inject

class TeamService @Inject constructor(
    private val store: TeamStore,
    private val participantService: ParticipantService,
    private val inviteService: InviteService,
    private val cache: AuthorizationCache
) {
    private val logger by Logger

    // hard to cache since we have also here shared teams
    // and when we cache a list of teams we can't know if a share
    // one got updated for a user. So keep it as it is for now
    suspend fun findForUser(user: UserPrincipal) = coroutineScope {
        // run both queries in parallel
        val ownTeams = async {
            store.findForOwner(user.userId)
                .map { it.toDomain(true) }
        }
        val sharedTeams = async {
            store.findSharedForUser(user.userId)
                .map { it.toDomain(false) }
        }

        buildList {
            addAll(ownTeams.await())
            addAll(sharedTeams.await())

            sortBy(Team::name)
        }
    }

    suspend fun findBySlug(user: UserPrincipal, teamSlug: String) = either {
        val teamEntity = store.findBySlug(teamSlug).bind()

        teamEntity.toDomain(user)
    }

    suspend fun joinTeam(user: UserPrincipal, slug: String, invite: UUID) = either<_, Unit> {
        val team = store.findBySlug(slug).bind()

        inviteService.claim(team, invite).bind()

        participantService.add(team, user)
    }

    suspend fun removeUserFromTeam(slug: String, user: UserPrincipal) = either {
        val team = store.findBySlug(slug).bind()

        participantService.delete(team, user)
    }

    suspend fun create(
        user: UserPrincipal,
        name: String,
    ): Team {
        val teamEntity = store.create(name, user.userId)

        logger.d { "Created ${teamEntity.display()}" }

        return teamEntity.toDomain(true)
    }

    suspend fun update(user: UserPrincipal, teamSlug: String, name: String?) = either {
        store.update(teamSlug = teamSlug, name = name)
            .bind()
            .toDomain(user)

        logger.d { "Updated Team($teamSlug)" }
    }

    suspend fun delete(slug: String) {
        store.deleteBySlug(slug)
        cache.deleteTeam(slug)
        logger.d { "Deleted Team($slug)" }
    }
}
