package chatter.models

import chatter.UserEntity
import chatter.lib.serialization.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class User(
    @Serializable(with = UUIDSerializer::class)
    val id: UUID,
    val username: String
)

fun UserEntity.toDomain() = User(
    id = id,
    username = username
)
