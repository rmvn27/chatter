package chatter.domain.services.teams

import arrow.core.raise.either
import chatter.ChannelEntity
import chatter.db.TeamChannelQueries
import chatter.db.asList
import chatter.db.asOne
import chatter.db.insert
import chatter.db.withDb
import chatter.domain.caches.TeamChannelCache
import chatter.domain.stores.TeamStore
import chatter.errors.ChannelNotFoundError
import chatter.lib.Slug
import chatter.models.toDomain
import java.util.UUID
import javax.inject.Inject

class ChannelService @Inject constructor(
    private val queries: TeamChannelQueries,
    private val teamStore: TeamStore,
    private val cache: TeamChannelCache,
    private val events: TeamEventsService
) {
    suspend fun findMany(teamSlug: String) = either {
        cache.getOrPut(teamSlug) {
            val team = teamStore.findBySlug(teamSlug).bind()

            queries.findByTeamId(team.id)
                .asList()
                .map(ChannelEntity::toDomain)
        }
    }

    suspend fun findChannelByTeamIdAndSlug(teamId: UUID, channelSlug: String) = either {
        queries.findByTeamAndSlug(teamId, channelSlug).asOne {
            ChannelNotFoundError(channelSlug)
        }
    }

    suspend fun findChannelBySlugs(teamSlug: String, channelSlug: String) = either {
        val team = teamStore.findBySlug(teamSlug).bind()

        findChannelByTeamIdAndSlug(team.id, channelSlug).bind()
    }

    suspend fun create(teamSlug: String, name: String) = either {
        val team = teamStore.findBySlug(teamSlug).bind()

        val entity = ChannelEntity(
            id = UUID.randomUUID(),
            teamId = team.id,
            name = name,
            slug = Slug.slugify(name),
        ).insert(queries::create)

        cache.delete(teamSlug)
        events.notifyChannelsChanged(team)
        entity.toDomain()
    }

    suspend fun delete(teamSlug: String, channelSlug: String) = either {
        val team = teamStore.findBySlug(teamSlug).bind()
        val channel = findChannelByTeamIdAndSlug(team.id, channelSlug).bind()

        withDb { queries.deleteById(channel.id) }
        cache.delete(teamSlug)
        events.notifyChannelsChanged(team)
    }
}
