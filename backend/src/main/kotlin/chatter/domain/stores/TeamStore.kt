package chatter.domain.stores

import arrow.core.raise.either
import chatter.TeamEntity
import chatter.db.TeamQueries
import chatter.db.asList
import chatter.db.asOne
import chatter.db.asOneInfallible
import chatter.db.insert
import chatter.db.withDb
import chatter.errors.TeamNotFoundError
import chatter.lib.Slug
import java.util.UUID
import javax.inject.Inject

// some services depend on some of the read operations for the teams
// so we create a separate store for just the crud ops
class TeamStore @Inject constructor(
    private val queries: TeamQueries
) {
    suspend fun findForOwner(ownerId: UUID) = queries.findForOwner(ownerId).asList()

    suspend fun findSharedForUser(userId: UUID) = queries.findSharedForUser(userId).asList()

    suspend fun findByIdInfallible(id: UUID) = queries.findById(id).asOneInfallible()

    suspend fun findBySlug(slug: String) = either {
        queries.findBySlug(slug).asOne { TeamNotFoundError(slug) }
    }

    suspend fun create(name: String, userId: UUID): TeamEntity {
        return TeamEntity(
            id = UUID.randomUUID(),
            name = name,
            slug = Slug.slugify(name),
            ownerId = userId
        ).insert(queries::create)
    }

    suspend fun update(teamSlug: String, name: String?) = either {
        val existingEntity = findBySlug(teamSlug).bind()

        // only update if name has changed
        if (name != null && name != existingEntity.name) {
            withDb {
                // while probably not the best solution
                // we don't update the slug to keep it stable
                // even though it may not reflect the new name
                queries.update(
                    id = existingEntity.id,
                    name = name,
                    slug = existingEntity.slug
                )
            }

            existingEntity.copy(name = name)
        } else {
            existingEntity
        }
    }

    suspend fun deleteBySlug(slug: String) = either {
        val team = findBySlug(slug).bind()

        withDb { queries.deleteById(team.id) }
    }
}
