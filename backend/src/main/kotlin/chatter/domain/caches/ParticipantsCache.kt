package chatter.domain.caches

import chatter.lib.cache.JsonKVCache
import chatter.lib.cache.RedisService
import chatter.models.TeamParticipant
import kotlinx.serialization.builtins.ListSerializer
import javax.inject.Inject

class ParticipantsCache @Inject constructor(
    redis: RedisService
) : JsonKVCache<List<TeamParticipant>>(redis) {
    override val prefix = "teamParticipants"
    override val serializer = ListSerializer(TeamParticipant.serializer())
}
