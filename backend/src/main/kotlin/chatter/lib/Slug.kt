package chatter.lib

import com.github.slugify.Slugify
import java.util.UUID

object Slug {
    private val slg = Slugify.builder().build()

    // slugify the base text and add a small random text
    // to it in the end to avoid collisions
    fun slugify(text: String): String {
        // take the first 4 chars out of a random UUID
        val randomSuffix = UUID.randomUUID().toString().take(4)

        return "${slg.slugify(text)}-$randomSuffix"
    }
}
