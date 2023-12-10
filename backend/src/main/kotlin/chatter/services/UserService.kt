package chatter.services

import arrow.core.raise.either
import chatter.UserEntity
import chatter.db.UserQueries
import chatter.db.asOptional
import chatter.db.insert
import chatter.errors.UserAlreadyExistsError
import chatter.lib.app.AppScope
import com.squareup.anvil.annotations.optional.SingleIn
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder
import java.util.UUID
import javax.inject.Inject

// since with `passwordEncoder` we have a bit of state in here
// register this service as a singleton
@SingleIn(AppScope::class)
class UserService @Inject constructor(
    private val queries: UserQueries
) {
    // use the password encode from spring for a simpler interface to bouncy castle
    // this has no other dependencies to other spring packages
    private val passwordEncoder = Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8()

    suspend fun findByUsername(username: String) = queries.findByUsername(username).asOptional()

    suspend fun create(
        username: String,
        password: String
    ) = either {
        // check for user with the same username
        if (findByUsername(username) != null) raise(UserAlreadyExistsError(username))

        val hashedPassword = passwordEncoder.encode(password)

        UserEntity(
            id = UUID.randomUUID(),
            username = username,
            password = hashedPassword
        ).insert(queries::insert)
    }

    fun verifyPassword(user: UserEntity, providedPassword: String) =
        passwordEncoder.matches(providedPassword, user.password)
}
