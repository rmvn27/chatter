package chatter.models

import chatter.lib.serialization.UUIDSerializer
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
data class ChannelMessage(
    val content: String,
    val timestamp: Long,
    @Serializable(with = UUIDSerializer::class)
    val userId: UUID?,

    // future proofing where we could have different
    // kinds of liveMessages like images
    val contentType: String = "text"
)

data class MessageEvent(
    val teamId: UUID,
    val channelId: UUID,
    val message: String,

    val timestamp: Long,
    val userId: UUID?
)
