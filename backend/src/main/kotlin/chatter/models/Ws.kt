package chatter.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

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

// in the future we could send the client more notifications than just the messages
@Serializable
sealed interface WsEvent {
    // Error happened during the handling of the commands
    @Serializable
    @SerialName("wsError")
    data class Error(val message: String, val code: Int) : WsEvent


    @Serializable
    @SerialName("messageReceived")
    data class MessageReceived(
        val message: Message,
    ) : WsEvent

    @Serializable
    @SerialName("participantListChanged")
    data class ParticipantListChanged(
        val teamSlug: String
    ) : WsEvent

    @Serializable
    @SerialName("channelListChanged")
    data class ChannelListChanged(
        val teamSlug: String
    ) : WsEvent
}
