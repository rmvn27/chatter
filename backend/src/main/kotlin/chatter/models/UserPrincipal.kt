package chatter.models

import io.ktor.server.auth.*
import kotlinx.serialization.Serializable

// authenticated user extracted out of a jwt token
//
// to not perform work that may be not needed
// we don't load the full user out of the database into it
// and just keep the information that was put into the jwt
@Serializable
data class UserPrincipal(
    val userId: String
) : Principal
