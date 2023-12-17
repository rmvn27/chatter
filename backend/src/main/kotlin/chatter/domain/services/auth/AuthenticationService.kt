package chatter.domain.services.auth

import arrow.core.raise.either
import chatter.UserRefreshTokenEntity
import chatter.db.UserRefreshTokenQueries
import chatter.db.asOptional
import chatter.db.insert
import chatter.db.withDb
import chatter.domain.services.UserService
import chatter.errors.BadAuthError
import chatter.errors.BadRefreshToken
import chatter.lib.log.getValue
import chatter.lib.toUUID
import chatter.models.UserAuthTokens
import chatter.models.UserPrincipal
import co.touchlab.kermit.Logger
import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.auth0.jwt.interfaces.Payload
import kotlinx.serialization.Serializable
import java.util.Date
import java.util.UUID
import javax.inject.Inject

class AuthenticationService @Inject constructor(
    private val userService: UserService,
    private val queries: UserRefreshTokenQueries,
    private val config: Config
) {
    private val logger by Logger

    val configRealm = config.realm
    val jwtVerifier: JWTVerifier by lazy {
        JWT.require(Algorithm.HMAC256(config.jwtSecret))
            .withIssuer(config.issuer)
            .withAudience(config.audience)
            .build()
    }

    fun getUserFromPayload(payload: Payload) = UserPrincipal(payload.subject.toUUID())

    suspend fun register(username: String, password: String) = either {
        val newUser = userService.create(username, password).bind()

        createTokens(newUser.id)
    }

    suspend fun login(username: String, password: String) = either {
        val user = userService.findByUsername(username) ?: raise(BadAuthError)
        if (!userService.verifyPassword(user, password)) raise(BadAuthError)

        logger.d { "Successful login for user: ${user.id}" }

        createTokens(user.id)
    }

    suspend fun regenerateTokens(refreshToken: String) = either {
        // check the validity if the refreshToken and look up the userId
        val refreshTokenEntity = queries.findByRefreshToken(refreshToken.toUUID())
            .asOptional()
            ?: raise(BadRefreshToken)
        val jwtToken = createJwtToken(refreshTokenEntity.userId)

        logger.d { "New tokens generated for user: ${refreshTokenEntity.userId}" }

        UserAuthTokens(
            refreshToken = refreshToken,
            accessToken = jwtToken
        )
    }

    suspend fun logout(refreshToken: String) = withDb {
        queries.deleteByRefreshToken(refreshToken.toUUID())
    }

    private suspend fun createTokens(userId: UUID): UserAuthTokens {
        val tokenEntity = UserRefreshTokenEntity(
            refreshToken = UUID.randomUUID(),
            userId = userId
        ).insert(queries::insert)

        return UserAuthTokens(
            refreshToken = tokenEntity.refreshToken.toString(),
            accessToken = createJwtToken(userId)
        )
    }

    private fun createJwtToken(userId: UUID) = JWT.create()
        .withIssuer(config.issuer)
        .withAudience(config.audience)
        .withSubject(userId.toString())
        // expire in 1 day
        .withExpiresAt(Date(System.currentTimeMillis() + (24 * 60 * 60 * 1000)))
        .sign(Algorithm.HMAC256(config.jwtSecret))

    @Serializable
    data class Config(
        val jwtSecret: String,
        // these there values are the same
        // since we are the issuer of the jwt itself
        // are also the intended audience
        //
        // technically we don't need them but its good
        // to keep good practise as they are required jwt claims
        // that prevent abuse of the token
        //
        // and realm is used for the `WWW-Authenticate` header
        // (see ktor docstring for the realm variable)
        val realm: String = "chatter",
        val issuer: String = "chatter",
        val audience: String = "chatter"
    )
}
