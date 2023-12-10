package chatter.services

import arrow.core.raise.either
import chatter.TeamParticipantEntity
import chatter.UserEntity
import chatter.db.TeamParticipantQueries
import chatter.db.asList
import chatter.db.insert
import chatter.db.withDb
import chatter.models.toDomain
import java.util.UUID
import javax.inject.Inject

class TeamParticipantService @Inject constructor(
    private val queries: TeamParticipantQueries,
    private val teamService: TeamService
) {
    suspend fun findMany(teamSlug: String) = either {
        val team = teamService.findEntity(teamSlug).bind()

        queries.findByTeamId(team.id)
            .asList()
            .map(UserEntity::toDomain)
    }

    suspend fun addParticipant(userId: UUID, teamId: UUID) = withDb {
        TeamParticipantEntity(
            userId = userId,
            teamId = teamId
        ).insert(queries::create)
    }

    suspend fun delete(teamSlug: String, userId: UUID) = either {
        val team = teamService.findEntity(teamSlug).bind()

        withDb {
            queries.delete(userId = userId, teamId = team.id)
        }
    }
}
