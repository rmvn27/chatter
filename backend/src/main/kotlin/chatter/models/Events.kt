package chatter.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// NOTE: These events are used for notifications that are sent over nats

@Serializable
sealed interface TeamEvent

@Serializable
@SerialName("channelListChanged")
data class ChannelListChangedEvent(val teamSlug: String) : TeamEvent {
    fun toWsEvent() = WsEvent.ChannelListChanged(teamSlug)
}

@Serializable
@SerialName("participantListChanged")
data class ParticipantListChangedEvent(val teamSlug: String) : TeamEvent {
    fun toWsEvent() = WsEvent.ParticipantListChanged(teamSlug)
}
