package chatter.models

import chatter.lib.serialization.UUIDSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

@Serializable
sealed interface WsCommand {
    @Serializable
    @SerialName("authenticate")
    data class Authenticate(val token: String) : WsCommand

    // enter a team by the slug
    @Serializable
    @SerialName("enterTeam")
    data class EnterTeam(val teamSlug: String) : WsCommand

    // enter a channel by the slug
    @Serializable
    @SerialName("enterChannel")
    data class EnterChannel(val channelSlug: String) : WsCommand

    // enter a team and channel by slug at the same time
    // can happen when a user enters the app by url
    @Serializable
    @SerialName("enterTeamAndChannel")
    data class EnterTeamAndChannel(
        val teamSlug: String,
        val channelSlug: String
    ) : WsCommand

    @Serializable
    @SerialName("leaveTeam")
    data object LeaveTeam : WsCommand

    @Serializable
    @SerialName("leaveChannel")
    data object LeaveChannel : WsCommand

    @Serializable
    @SerialName("sendTextMessage")
    data class SendTextMessage(val message: String) : WsCommand
}

@Serializable
sealed interface WsEvent {
    // Error happened during the handling of the commands
    @Serializable
    @SerialName("wsError")
    data class Error(val message: String, val code: Int) : WsEvent


    @Serializable
    @SerialName("message")
    data class Message(
        @Serializable(with = UUIDSerializer::class)
        val userId: UUID?,
        val timestamp: Long,

        val content: String,
        val contentType: String = "text"
    ) : WsEvent
}
