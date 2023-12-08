package chatter.models

import chatter.TeamEntity
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
