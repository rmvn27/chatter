package chatter.models

import chatter.lib.serialization.UUIDSerializer
import io.ktor.server.auth.*
import kotlinx.serialization.Serializable
import java.util.UUID

// authenticated user extracted out of a jwt token
//
// to not perform work that may be not needed
// we don't load the full user out of the database into it
// and just keep the information that was put into the jwt
data class UserPrincipal(
    val userId: UUID
) : Principal

@Serializable
data class UserAuthTokens(
    val accessToken: String,
    @Serializable(with = UUIDSerializer::class)
    val refreshToken: UUID
)
