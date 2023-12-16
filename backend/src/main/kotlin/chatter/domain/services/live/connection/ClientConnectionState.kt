package chatter.domain.services.live.connection

import java.util.UUID

sealed interface ClientConnectionState {
    data object Base : ClientConnectionState

    data class InTeam(val teamId: UUID) : ClientConnectionState
    data class InTeamAndChannel(val teamId: UUID, val channelId: UUID) : ClientConnectionState
}

val ClientConnectionState.teamId: UUID?
    get() = when (this) {
        is ClientConnectionState.Base -> null
        is ClientConnectionState.InTeam -> this.teamId
        is ClientConnectionState.InTeamAndChannel -> this.teamId
    }

val ClientConnectionState.channelId: UUID?
    get() = when (this) {
        is ClientConnectionState.Base -> null
        is ClientConnectionState.InTeam -> null
        is ClientConnectionState.InTeamAndChannel -> this.channelId
    }
