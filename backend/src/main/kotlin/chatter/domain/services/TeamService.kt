package chatter.domain.services

import arrow.core.raise.either
import chatter.domain.caches.AuthorizationCache
import chatter.domain.stores.TeamStore
import chatter.lib.log.getValue
import chatter.models.toDomain
import co.touchlab.kermit.Logger
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.util.UUID
import javax.inject.Inject

class TeamService @Inject constructor(
    private val store: TeamStore,
    private val participantService: TeamParticipantService,
    private val inviteService: TeamInviteService,
    private val cache: AuthorizationCache
) {
    private val logger by Logger

    // hard to cache since we have also here shared teams
    // and when we cache a list of teams we can't know if a share
    // one got updated for a user. So keep it as it is for now
    suspend fun findForUser(userId: UUID) = coroutineScope {
        // run both queries in parallel
        val ownTeams = async {
            store.findForOwner(userId)
                .map { it.toDomain(true) }
        }
        val sharedTeams = async {
            store.findSharedForUser(userId)
                .map { it.toDomain(false) }
        }

        (ownTeams.await() + sharedTeams.await()).sortedBy { it.name }
    }

    suspend fun findBySlug(teamSlug: String, userId: UUID) = either {
        val teamEntity = store.findBySlug(teamSlug).bind()

        teamEntity.toDomain(userId)
    }

    suspend fun joinTeam(slug: String, userId: UUID, invite: UUID) = either<_, Unit> {
        val team = store.findBySlug(slug).bind()

        inviteService.claim(team, invite).bind()

        participantService.add(team, userId)
    }

    suspend fun removeUserFromTeam(slug: String, userId: UUID) = either {
        val team = store.findBySlug(slug).bind()

        participantService.delete(team, userId)
    }

    suspend fun create(
        name: String,
        userId: UUID
    ) = store.create(name, userId)
        .toDomain(true)
        .also { logger.d { "Created Team: ${it.slug}" } }

    suspend fun update(userId: UUID, teamSlug: String, name: String?) = either {
        store.update(teamSlug = teamSlug, name = name)
            .bind()
            .toDomain(userId)
    }

    suspend fun delete(slug: String) {
        store.deleteBySlug(slug)
        cache.deleteTeam(slug)
    }
}
