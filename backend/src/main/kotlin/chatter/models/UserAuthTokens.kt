package chatter.models

import chatter.lib.serialization.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class UserAuthTokens(
    val accessToken: String,
    @Serializable(with = UUIDSerializer::class)
    val refreshToken: UUID
)
