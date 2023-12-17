package chatter.domain.services.teams

import arrow.core.raise.either
import chatter.ParticipantEntity
import chatter.TeamEntity
import chatter.db.TeamParticipantQueries
import chatter.db.UserQueries
import chatter.db.asList
import chatter.db.asOneInfallible
import chatter.db.insert
import chatter.db.withDb
import chatter.domain.caches.AuthorizationCache
import chatter.domain.caches.ParticipantsCache
import chatter.domain.stores.TeamStore
import chatter.models.Participant
import chatter.models.UserPrincipal
import chatter.models.toDomain
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.util.UUID
import javax.inject.Inject

class ParticipantService @Inject constructor(
    private val queries: TeamParticipantQueries,
    private val teamStore: TeamStore,
    private val userQueries: UserQueries,
    private val authCache: AuthorizationCache,
    private val cache: ParticipantsCache,
    private val events: TeamEventsService
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
                    userQueries.findById(team.ownerId)
                        .asOneInfallible()
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
        val entity = ParticipantEntity(
            teamId = team.id,
            userId = user.userId
        ).insert(queries::create)

        // since we serialize the complete list, we heave to reconstruct the cache
        // again. So we delete it here, then it can be created again in `findMany`
        cache.delete(team.slug)
        events.notifyParticipantsChanged(listOf(team))

        return entity
    }

    // as the user has changed we have to clear all effected caches and
    // also can notify the current connected clients that their participant
    // list has been affected
    suspend fun handleUserChange(user: UserPrincipal) = coroutineScope {
        val teams = findTeamsForUser(user)

        // clear the caches
        teams.map { async { cache.delete(it.slug) } }.awaitAll()
        events.notifyParticipantsChanged(teams)
    }

    suspend fun delete(teamSlug: String, userId: UUID) = either {
        val team = teamStore.findBySlug(teamSlug).bind()

        // just assume that the userId is correct and convert it to the `UserPrincipal`
        delete(team, UserPrincipal(userId))
    }

    suspend fun delete(team: TeamEntity, user: UserPrincipal) {
        withDb { queries.delete(userId = user.userId, teamId = team.id) }
        authCache.removeParticipant(team.slug, user)
        cache.delete(team.slug)
        events.notifyParticipantsChanged(listOf(team))
    }

    // find all teams where the user participates. these are their own teams
    // and the ones where they were invited
    private suspend fun findTeamsForUser(user: UserPrincipal) = coroutineScope {
        val teamsForOwner = async { teamStore.findForOwner(user.userId) }
        val participating = async { queries.findTeamsForParticipant(user.userId).asList() }

        teamsForOwner.await() + participating.await()
    }
}
