package chatter.lib.serialization

import kotlinx.serialization.json.Json

// different json parsers that are used all over the application
//
// they all have different settings depending on what are needed
object JsonParsers {
    val nonStrict by lazy {
        Json { ignoreUnknownKeys = true }
    }

    val strict by lazy {
        Json { ignoreUnknownKeys = false }
    }
}
