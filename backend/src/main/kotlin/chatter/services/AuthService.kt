package chatter.services

import arrow.core.Either
import arrow.core.left
import arrow.core.raise.either
import arrow.core.right
import chatter.UserEntity
import chatter.UserRefreshTokenEntity
import chatter.db.UserRefreshTokenQueries
import chatter.db.asOptional
import chatter.db.withDb
import chatter.errors.ApplicationError
import chatter.errors.BadAuthError
import chatter.errors.BadRefreshToken
import chatter.models.UserAuthTokens
import chatter.models.UserPrincipal
import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import io.ktor.server.auth.jwt.*
import kotlinx.serialization.Serializable
import java.util.Date
import java.util.UUID
import javax.inject.Inject

class AuthService @Inject constructor(
    private val userService: UserService,
    private val queries: UserRefreshTokenQueries,
    private val config: Config
) {
    suspend fun register(username: String, password: String) = either {
        val newUser = userService.create(username, password).bind()

        createTokens(newUser)
    }

    suspend fun login(username: String, password: String): Either<ApplicationError, UserAuthTokens> {
        val user = userService.findByUsername(username) ?: return BadAuthError.left()
        if (!userService.verifyPassword(user, password)) return BadAuthError.left()

        return createTokens(user).right()
    }

    suspend fun regenerateTokens(refreshToken: UUID): Either<ApplicationError, UserAuthTokens> {
        // check the validity if the refreshToken and look up the userId
        val refreshTokenEntity = queries.findByRefreshToken(refreshToken)
            .asOptional()
            ?: return BadRefreshToken.left()
        val jwtToken = createJwtToken(refreshTokenEntity.userId)

        return UserAuthTokens(
            refreshToken = refreshToken.toString(),
            authToken = jwtToken
        ).right()
    }

    suspend fun logout(refreshToken: UUID) = withDb {
        queries.deleteByRefreshToken(refreshToken)
    }

    // create the auth functionality that can be directly installed into ktor
    context(JWTAuthenticationProvider.Config)
    fun createJwtAuthentication() {
        realm = config.realm
        verifier(
            issuer = config.issuer,
            audience = config.audience,
            algorithm = Algorithm.HMAC256(config.jwtSecret)
        )

        validate { UserPrincipal(it.payload.subject) }
    }

    private suspend fun createTokens(user: UserEntity): UserAuthTokens {
        val tokenEntity = UserRefreshTokenEntity(
            refreshToken = UUID.randomUUID(),
            userId = user.id
        )

        withDb { queries.insert(tokenEntity) }



        return UserAuthTokens(
            refreshToken = tokenEntity.refreshToken.toString(),
            authToken = createJwtToken(user.id)
        )
    }

    private fun createJwtToken(userId: UUID) = JWT.create()
        .withIssuer(config.issuer)
        .withAudience(config.audience)
        .withSubject(userId.toString())
        // expire in 15 minutes
        .withExpiresAt(Date(System.currentTimeMillis() + (15 * 60 * 1000)))
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
