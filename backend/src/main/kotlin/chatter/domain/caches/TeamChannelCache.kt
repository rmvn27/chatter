package chatter.domain.caches

import chatter.lib.cache.JsonKVCache
import chatter.lib.cache.RedisService
import chatter.models.TeamChannel
import kotlinx.serialization.builtins.ListSerializer
import javax.inject.Inject

class TeamChannelCache @Inject constructor(
    redis: RedisService
) : JsonKVCache<List<TeamChannel>>(redis) {
    override val prefix = "teamChannels"
    override val serializer = ListSerializer(TeamChannel.serializer())
}
