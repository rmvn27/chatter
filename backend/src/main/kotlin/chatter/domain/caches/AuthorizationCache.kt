package chatter.domain.caches

import chatter.lib.cache.RedisService
import chatter.lib.toUUID
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.future.await
import java.util.UUID
import javax.inject.Inject

class AuthorizationCache @Inject constructor(
    private val redis: RedisService
) {
    suspend fun getTeamOwner(teamSlug: String): UUID? {
        return redis.commands.get(teamOwnerKey(teamSlug)).await()?.let(String::toUUID)
    }

    suspend fun putTeamOwner(teamSlug: String, owner: UUID) {
        redis.commands.set(teamOwnerKey(teamSlug), owner.toString()).await()
    }


    suspend fun checkForTeamParticipant(teamSlug: String, user: UUID): Boolean {
        return redis.commands.sismember(
            teamParticipantsKey(teamSlug),
            user.toString()
        ).await()
    }

    suspend fun putTeamParticipant(teamSlug: String, user: UUID) {
        redis.commands.sadd(
            teamParticipantsKey(teamSlug),
            user.toString()
        ).await()
    }

    suspend fun removeParticipant(teamSlug: String, user: UUID) {
        redis.commands.srem(
            teamParticipantsKey(teamSlug),
            user.toString()
        ).await()
    }

    suspend fun deleteTeam(teamSlug: String) = coroutineScope {
        val rmTeamOwner = async {
            redis.commands.del(teamOwnerKey(teamSlug)).await()
        }

        val rmParticipants = async {
            redis.commands.del(teamParticipantsKey(teamSlug)).await()
        }

        rmParticipants.await()
        rmTeamOwner.await()
    }

    private fun teamOwnerKey(slug: String) = "authorization:$slug:teamOwner"
    private fun teamParticipantsKey(slug: String) = "authorization:$slug:participants"
}
