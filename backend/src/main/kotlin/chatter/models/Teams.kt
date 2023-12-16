package chatter.models

import chatter.ChannelEntity
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

fun TeamEntity.toDomain(user: UserPrincipal) = toDomain(ownerId == user.userId)
fun TeamEntity.toDomain(isOwner: Boolean) = Team(
    id = id,
    slug = slug,
    name = name,
    isOwner = isOwner
)

// this class will be inlined and is only used for type safety
@Serializable
@JvmInline
value class Invite(
    @Serializable(with = UUIDSerializer::class)
    val invite: UUID
)

@Serializable
data class Participant(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val name: String,
    val username: String,
    val teamOwner: Boolean
)

fun UserEntity.toDomain(teamOwner: Boolean) = Participant(
    id = id,
    username = username,
    teamOwner = teamOwner,
    name = displayName
)


@Serializable
data class Channel(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val name: String,
    val slug: String
)


fun ChannelEntity.toDomain() = Channel(
    id = id,
    name = name,
    slug = slug
)
