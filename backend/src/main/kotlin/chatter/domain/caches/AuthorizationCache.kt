package chatter.domain.caches

import chatter.lib.cache.RedisService
import chatter.lib.serialization.JsonParsers
import chatter.models.UserPrincipal
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.future.await
import kotlinx.serialization.encodeToString
import javax.inject.Inject

class AuthorizationCache @Inject constructor(
    private val redis: RedisService
) {
    suspend fun getTeamOwner(teamSlug: String): UserPrincipal? {
        return redis.commands.get(teamOwnerKey(teamSlug)).await()?.decodeToUser()
    }

    suspend fun putTeamOwner(teamSlug: String, owner: UserPrincipal) {
        redis.commands.set(teamOwnerKey(teamSlug), owner.encode()).await()
    }

    suspend fun checkForTeamParticipant(teamSlug: String, user: UserPrincipal): Boolean {
        return redis.commands.sismember(
            teamParticipantsKey(teamSlug),
            user.encode()
        ).await()
    }

    suspend fun putTeamParticipant(teamSlug: String, user: UserPrincipal) {
        redis.commands.sadd(
            teamParticipantsKey(teamSlug),
            user.encode()
        ).await()
    }

    suspend fun removeParticipant(teamSlug: String, user: UserPrincipal) {
        redis.commands.srem(
            teamParticipantsKey(teamSlug),
            user.encode()
        ).await()
    }

    suspend fun deleteTeam(teamSlug: String): Unit = coroutineScope {
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

    private fun UserPrincipal.encode() = JsonParsers.strict.encodeToString(this)
    private fun String.decodeToUser() = JsonParsers.strict.decodeFromString<UserPrincipal>(this)
}
