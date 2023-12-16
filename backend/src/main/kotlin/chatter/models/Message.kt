package chatter.models

import chatter.lib.serialization.UUIDSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

// external model for a message
@Serializable
data class Message(
    val content: MessageContent,
    val timestamp: Long,
    @Serializable(with = UUIDSerializer::class)
    val userId: UUID?,
)

// future proofing where we could have different
// kinds of liveMessages like images
@Serializable
sealed interface MessageContent {
    // stringly typed version of the content
    // eg a picture could have in the future either a base64 encoding
    // or point to a object storage location
    val content: String

    @Serializable
    @SerialName("text")
    data class Text(override val content: String) : MessageContent
}
