package chatter.services

import arrow.core.Either
import arrow.core.left
import arrow.core.raise.either
import arrow.core.right
import chatter.TeamInviteEntity
import chatter.db.TeamInviteQueries
import chatter.db.asList
import chatter.db.asOptional
import chatter.db.withDb
import chatter.errors.ApplicationError
import chatter.errors.TeamInviteNotFoundError
import chatter.models.TeamInvite
import java.util.UUID
import javax.inject.Inject

class TeamInviteService @Inject constructor(
    private val queries: TeamInviteQueries,
    private val teamService: TeamService,
    private val participantService: TeamParticipantService
) {
    suspend fun findMany(
        teamSlug: String
    ) = either {
        val team = teamService.findEntity(teamSlug).bind()

        queries.findByTeam(team.id)
            .asList()
            .map { TeamInvite(it.invite) }
    }

    suspend fun claim(userId: UUID, teamSlug: String, invite: UUID) = either {
        val team = teamService.findEntity(teamSlug).bind()
        findOrError(invite).bind()

        participantService.addParticipant(userId, team.id)

        delete(invite)
    }

    suspend fun create(teamSlug: String) = either {
        val team = teamService.findEntity(teamSlug).bind()
        val invite = TeamInvite(UUID.randomUUID())

        withDb {
            val entity = TeamInviteEntity(
                invite = invite.invite,
                teamId = team.id
            )
            queries.create(entity)
        }

        invite
    }

    suspend fun delete(invite: UUID) = withDb {
        queries.delete(invite)
    }

    private suspend fun findOrError(invite: UUID): Either<ApplicationError, TeamInviteEntity> {
        val entity = queries.findByInvite(invite)
            .asOptional()
            ?: return TeamInviteNotFoundError(invite).left()

        return entity.right()
    }
}
