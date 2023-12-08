package chatter.services

import chatter.TeamInviteEntity
import chatter.db.TeamInviteQueries
import chatter.db.asList
import chatter.db.withDb
import chatter.models.TeamInvite
import java.util.UUID
import javax.inject.Inject

class TeamInviteService @Inject constructor(
    private val queries: TeamInviteQueries
) {
    suspend fun findMany(
        teamId: UUID
    ) = queries.findByTeam(teamId)
        .asList()
        .map { TeamInvite(it.invite) }

    suspend fun create(teamId: UUID): TeamInvite {
        val invite = TeamInvite(UUID.randomUUID())

        withDb {
            val entity = TeamInviteEntity(
                invite = invite.invite,
                teamId = teamId
            )
            queries.create(entity)
        }

        return invite
    }

    suspend fun delete(invite: UUID) = withDb {
        queries.delete(invite)
    }
}
