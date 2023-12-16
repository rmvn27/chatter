package chatter.models

import kotlinx.serialization.Serializable

@Serializable
data class UserAuthTokens(
    val accessToken: String,
    val refreshToken: String
)
