package chatter.domain.services

import arrow.core.raise.either
import chatter.TeamChannelEntity
import chatter.db.TeamChannelQueries
import chatter.db.asList
import chatter.db.asOne
import chatter.db.insert
import chatter.db.withDb
import chatter.domain.caches.TeamChannelCache
import chatter.domain.stores.TeamStore
import chatter.errors.ChannelNotFoundError
import chatter.lib.Slug
import chatter.models.TeamChannel
import chatter.models.toDomain
import java.util.UUID
import javax.inject.Inject

class TeamChannelService @Inject constructor(
    private val queries: TeamChannelQueries,
    private val teamStore: TeamStore,
    private val cache: TeamChannelCache
) {

    suspend fun findMany(teamSlug: String) = either {
        cache.getOrPut(teamSlug) {
            val team = teamStore.findBySlug(teamSlug).bind()

            queries.findByTeamId(team.id)
                .asList()
                .map(TeamChannelEntity::toDomain)
                .sortedBy(TeamChannel::name)
        }
    }

    suspend fun create(teamSlug: String, name: String) = either {
        val team = teamStore.findBySlug(teamSlug).bind()

        val entity = TeamChannelEntity(
            id = UUID.randomUUID(),
            teamId = team.id,
            name = name,
            slug = Slug.slugify(name),
        ).insert(queries::create)

        cache.delete(teamSlug)
        entity.toDomain()
    }

    suspend fun delete(teamSlug: String, channelSlug: String) = either {
        val channel = findChannelBySlug(teamSlug, channelSlug).bind()

        withDb { queries.deleteById(channel.id) }
        cache.delete(teamSlug)
    }

    private suspend fun findChannelBySlug(teamSlug: String, channelSlug: String) = either {
        val team = teamStore.findBySlug(teamSlug).bind()

        queries.findByTeamAndSlug(team.id, channelSlug).asOne {
            ChannelNotFoundError(channelSlug)
        }
    }
}
