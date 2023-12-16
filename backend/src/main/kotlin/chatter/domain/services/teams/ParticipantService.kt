package chatter.domain.services.teams

import arrow.core.raise.either
import chatter.ParticipantEntity
import chatter.TeamEntity
import chatter.db.TeamParticipantQueries
import chatter.db.asList
import chatter.db.insert
import chatter.db.withDb
import chatter.domain.caches.AuthorizationCache
import chatter.domain.caches.ParticipantsCache
import chatter.domain.services.UserService
import chatter.domain.stores.TeamStore
import chatter.models.Participant
import chatter.models.UserPrincipal
import chatter.models.toDomain
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.util.UUID
import javax.inject.Inject

class ParticipantService @Inject constructor(
    private val queries: TeamParticipantQueries,
    private val teamStore: TeamStore,
    private val userService: UserService,
    private val authCache: AuthorizationCache,
    private val cache: ParticipantsCache
) {
    suspend fun findMany(teamSlug: String) = either {
        cache.getOrPut(teamSlug) {
            val team = teamStore.findBySlug(teamSlug).bind()

            coroutineScope {
                val participants = async {
                    queries.findByTeamId(team.id)
                        .asList()
                        .map { it.toDomain(false) }
                }

                val owner = async {
                    userService.findByIdInfallible(team.ownerId)
                        .toDomain(true)
                }


                buildList {
                    add(owner.await())
                    addAll(participants.await())

                    //this can't happen in sql since we have to add the owner to it
                    sortBy(Participant::name)
                }
            }
        }
    }

    suspend fun add(team: TeamEntity, user: UserPrincipal): ParticipantEntity {
        // since we serialize the complete list, we heave to reconstruct the cache
        // again. So we delete it here, then it can be created again in `findMany`
        cache.delete(team.slug)

        return ParticipantEntity(
            teamId = team.id,
            userId = user.userId
        ).insert(queries::create)
    }

    // keep the
    suspend fun delete(teamSlug: String, userId: UUID) = either {
        val team = teamStore.findBySlug(teamSlug).bind()

        // just assume that the userId is correct and convert it to the `UserPrincipal`
        delete(team, UserPrincipal(userId))
    }

    suspend fun delete(team: TeamEntity, user: UserPrincipal) {
        withDb { queries.delete(userId = user.userId, teamId = team.id) }
        authCache.removeParticipant(team.slug, user)
        cache.delete(team.slug)
    }
}
