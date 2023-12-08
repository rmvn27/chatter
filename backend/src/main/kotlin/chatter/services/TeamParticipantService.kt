package chatter.services

import chatter.TeamParticipantEntity
import chatter.db.TeamParticipantQueries
import chatter.db.withDb
import java.util.UUID
import javax.inject.Inject

class TeamParticipantService @Inject constructor(
    private val queries: TeamParticipantQueries
) {

    suspend fun addParticipant(userId: UUID, teamId: UUID) = withDb {
        val entity = TeamParticipantEntity(
            userId = userId,
            teamId = teamId
        )

        queries.create(entity)

        entity
    }
}
