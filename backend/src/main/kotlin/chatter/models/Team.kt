package chatter.models

import chatter.TeamChannelEntity
import chatter.TeamEntity
import chatter.UserEntity
import chatter.lib.serialization.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class Team(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val slug: String,
    val name: String,

    val isOwner: Boolean
)

fun TeamEntity.toDomain(userId: UUID) = toDomain(ownerId == userId)
fun TeamEntity.toDomain(isOwner: Boolean) = Team(
    id = id,
    slug = slug,
    name = name,
    isOwner = isOwner
)

// this call will be inlined and is only used for type safety
@Serializable
@JvmInline
value class TeamInvite(
    @Serializable(with = UUIDSerializer::class)
    val invite: UUID
)

@Serializable
data class TeamParticipant(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val name: String,
    val username: String,
    val teamOwner: Boolean
)

fun UserEntity.toDomain(teamOwner: Boolean) = TeamParticipant(
    id = id,
    username = username,
    teamOwner = teamOwner,
    name = displayName
)


@Serializable
data class TeamChannel(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val name: String,
    val slug: String
)


fun TeamChannelEntity.toDomain() = TeamChannel(
    id = id,
    name = name,
    slug = slug
)
