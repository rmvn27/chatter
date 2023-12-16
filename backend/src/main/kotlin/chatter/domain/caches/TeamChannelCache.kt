package chatter.domain.caches

import chatter.lib.cache.JsonKVCache
import chatter.lib.cache.RedisService
import chatter.models.Channel
import kotlinx.serialization.builtins.ListSerializer
import javax.inject.Inject

class TeamChannelCache @Inject constructor(
    redis: RedisService
) : JsonKVCache<List<Channel>>(redis) {
    override val prefix = "channels"
    override val serializer = ListSerializer(Channel.serializer())
}
