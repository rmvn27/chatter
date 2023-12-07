package chatter.models

import kotlinx.serialization.Serializable

@Serializable
data class UserAuthTokens(
    val authToken: String,
    val refreshToken: String
)
