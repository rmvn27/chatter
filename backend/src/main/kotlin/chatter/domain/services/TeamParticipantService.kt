package chatter.domain.services

import arrow.core.raise.either
import chatter.TeamEntity
import chatter.TeamParticipantEntity
import chatter.db.TeamParticipantQueries
import chatter.db.UserQueries
import chatter.db.asList
import chatter.db.asOneInfallible
import chatter.db.insert
import chatter.db.withDb
import chatter.domain.stores.TeamStore
import chatter.models.TeamParticipant
import chatter.models.toDomain
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.util.UUID
import javax.inject.Inject

class TeamParticipantService @Inject constructor(
    private val queries: TeamParticipantQueries,
    private val teamStore: TeamStore,
    private val userQueries: UserQueries
) {
    suspend fun findMany(teamSlug: String) = either {
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
                sortBy(TeamParticipant::username)
            }
        }
    }

    suspend fun add(team: TeamEntity, userId: UUID): TeamParticipantEntity {
        return TeamParticipantEntity(
            teamId = team.id,
            userId = userId
        ).insert(queries::create)
    }

    suspend fun delete(teamSlug: String, userId: UUID) = either {
        val team = teamStore.findBySlug(teamSlug).bind()

        delete(team, userId)
    }

    suspend fun delete(team: TeamEntity, userId: UUID) = withDb {
        queries.delete(userId = userId, teamId = team.id)
    }
}
