package chatter.services

import arrow.core.raise.either
import chatter.TeamEntity
import chatter.db.TeamQueries
import chatter.db.asList
import chatter.db.asOptional
import chatter.db.insert
import chatter.db.withDb
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

    suspend fun findBySlug(userId: UUID, teamSlug: String) = either {
        val teamEntity = findEntity(teamSlug).bind()

        teamEntity.toDomain(userId)
    }

    suspend fun findEntity(slug: String) = either {
        val entity = queries.findBySlug(slug)
            .asOptional()
            ?: raise(ProjectNotFoundError(slug))

        entity
    }

    suspend fun create(name: String, userId: UUID): Team {
        val entity = TeamEntity(
            id = UUID.randomUUID(),
            name = name,
            slug = Slug.slugify(name),
            ownerId = userId
        ).insert(queries::create)

        return entity.toDomain(true)
    }

    suspend fun update(userId: UUID, teamSlug: String, name: String?) = either {
        val existingEntity = findEntity(teamSlug).bind()

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

    suspend fun delete(slug: String) = withDb {
        queries.deleteBySlug(slug)
    }
}
