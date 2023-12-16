package chatter.domain.caches

import chatter.lib.cache.JsonKVCache
import chatter.lib.cache.RedisService
import chatter.models.Participant
import kotlinx.serialization.builtins.ListSerializer
import javax.inject.Inject

class ParticipantsCache @Inject constructor(
    redis: RedisService
) : JsonKVCache<List<Participant>>(redis) {
    override val prefix = "participants"
    override val serializer = ListSerializer(Participant.serializer())
}
