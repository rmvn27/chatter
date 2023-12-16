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
//
// also we shouldn't leak information from the http layer into the domain
// layer but we just import the `Principal` interface and nothing else
@JvmInline
@Serializable
value class UserPrincipal(
    @Serializable(with = UUIDSerializer::class)
    val userId: UUID
) : Principal
