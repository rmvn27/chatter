package chatter.domain.services

import arrow.core.raise.either
import chatter.UserEntity
import chatter.db.UserQueries
import chatter.db.asOneInfallible
import chatter.db.asOptional
import chatter.db.insert
import chatter.db.withDb
import chatter.domain.caches.ParticipantsCache
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
    private val queries: UserQueries,
    private val participantsCache: ParticipantsCache
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
            displayName = username,
            password = hashedPassword
        ).insert(queries::insert)
    }

    fun verifyPassword(user: UserEntity, providedPassword: String) =
        passwordEncoder.matches(providedPassword, user.password)

    suspend fun update(userId: UUID, password: String?, name: String?) {
        val user = queries.findById(userId).asOneInfallible()

        var changed = false
        var newPassword = user.password
        var newDisplayName = user.displayName

        if (password != null && user.password != password) {
            newPassword = passwordEncoder.encode(password)
            changed = true
        }

        if (name != null && user.displayName != name) {
            newDisplayName = name
            changed = true
        }


        if (changed) {
            withDb {
                queries.update(
                    id = userId,
                    password = newPassword,
                    displayName = newDisplayName
                )
            }

            // edge case: since the participants of a team
            // are cached inside a list we just have to clear the whole cache
            participantsCache.deleteAll()
        }
    }

    suspend fun delete(user: UUID) {
        withDb { queries.delete(user) }

        // edge case: since the participants of a team
        // are cached inside a list we just have to clear the whole cache
        participantsCache.deleteAll()
    }
}
