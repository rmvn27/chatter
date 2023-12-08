package chatter.services

import arrow.core.Either
import arrow.core.left
import arrow.core.raise.either
import arrow.core.right
import chatter.TeamEntity
import chatter.db.TeamQueries
import chatter.db.asList
import chatter.db.asOptional
import chatter.db.withDb
import chatter.errors.ApplicationError
import chatter.errors.ProjectNotFoundError
import chatter.lib.Slug
import chatter.models.Team
import chatter.models.toDomain
import java.util.UUID
import javax.inject.Inject

class TeamService @Inject constructor(
    private val queries: TeamQueries
) {
    suspend fun findForUser(userId: UUID): List<Team> {
        val ownTeams = queries.findForOwner(userId)
            .asList()
            .map { it.toDomain(true) }
        val sharedTeams = queries.findSharedForUser(userId)
            .asList()
            .map { it.toDomain(false) }

        return ownTeams + sharedTeams
    }

    suspend fun findById(userId: UUID, teamId: UUID) = either {
        val teamEntity = findOrError(teamId).bind()

        teamEntity.toDomain(userId)
    }

    suspend fun create(name: String, userId: UUID): Team {
        val entity = TeamEntity(
            id = UUID.randomUUID(),
            name = name,
            slug = Slug.slugify(name),
            ownerId = userId
        )

        withDb { queries.create(entity) }

        return entity.toDomain(true)
    }

    suspend fun update(userId: UUID, teamId: UUID, name: String?) = either {
        val existingEntity = findOrError(teamId).bind()

        if (name != null && name != existingEntity.name) {
            val newSlug = Slug.slugify(name)

            withDb {
                queries.update(
                    id = existingEntity.id,
                    name = name,
                    slug = newSlug
                )
            }

            existingEntity.copy(
                name = name,
                slug = newSlug
            ).toDomain(userId)
        } else {
            existingEntity.toDomain(userId)
        }
    }

    suspend fun delete(id: UUID) = withDb {
        queries.deleteById(id)
    }

    private suspend fun findOrError(teamId: UUID): Either<ApplicationError, TeamEntity> {
        val entity = queries.findById(teamId)
            .asOptional()
            ?: return ProjectNotFoundError(teamId).left()

        return entity.right()
    }
}
