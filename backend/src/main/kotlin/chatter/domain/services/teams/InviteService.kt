package chatter.domain.services.teams

import arrow.core.raise.either
import chatter.InviteEntity
import chatter.TeamEntity
import chatter.db.TeamInviteQueries
import chatter.db.asList
import chatter.db.asOptional
import chatter.db.withDb
import chatter.domain.stores.TeamStore
import chatter.errors.InvalidTeamInviteError
import chatter.models.Invite
import java.util.UUID
import javax.inject.Inject

class InviteService @Inject constructor(
    private val queries: TeamInviteQueries,
    private val teamStore: TeamStore
) {
    suspend fun findMany(
        teamSlug: String
    ) = either {
        val team = teamStore.findBySlug(teamSlug).bind()

        queries.findByTeam(team.id)
            .asList()
            .map { Invite(it.invite) }
    }

    // a user can claim an invite to join the team
    // for this we search for the invite and if found remove it
    suspend fun claim(team: TeamEntity, invite: UUID) = either {
        val inviteEntity = queries.findByInvite(invite = invite, teamId = team.id)
            .asOptional()
            ?: raise(InvalidTeamInviteError)

        delete(inviteEntity.invite)
    }

    suspend fun create(teamSlug: String) = either {
        val team = teamStore.findBySlug(teamSlug).bind()
        val invite = Invite(UUID.randomUUID())

        withDb {
            val entity = InviteEntity(
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
}
