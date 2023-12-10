package chatter.domain.services

import arrow.core.raise.either
import chatter.TeamEntity
import chatter.TeamParticipantEntity
import chatter.UserEntity
import chatter.db.TeamParticipantQueries
import chatter.db.asList
import chatter.db.insert
import chatter.db.withDb
import chatter.domain.stores.TeamStore
import chatter.models.toDomain
import java.util.UUID
import javax.inject.Inject

class TeamParticipantService @Inject constructor(
    private val queries: TeamParticipantQueries,
    private val teamStore: TeamStore
) {
    suspend fun findMany(teamSlug: String) = either {
        val team = teamStore.findBySlug(teamSlug).bind()

        queries.findByTeamId(team.id)
            .asList()
            .map(UserEntity::toDomain)
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
