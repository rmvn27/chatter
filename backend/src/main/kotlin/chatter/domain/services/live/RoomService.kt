package chatter.domain.services.live

import arrow.core.raise.either
import chatter.domain.services.auth.AuthorizationService
import chatter.domain.services.live.rooms.TeamRoom
import chatter.domain.services.teams.ChannelService
import chatter.domain.services.teams.MessageService
import chatter.domain.stores.TeamStore
import chatter.lib.app.AppScope
import chatter.lib.coroutines.Locked
import chatter.lib.log.getValue
import chatter.lib.service.StatefulService
import co.touchlab.kermit.Logger
import com.squareup.anvil.annotations.ContributesMultibinding
import com.squareup.anvil.annotations.optional.SingleIn
import java.util.UUID
import javax.inject.Inject

@SingleIn(AppScope::class)
@ContributesMultibinding(AppScope::class)
class RoomService @Inject constructor(
    private val teamStore: TeamStore,
    private val channelService: ChannelService,
    private val messageService: MessageService,
    private val authService: AuthorizationService
) : StatefulService {
    private val logger by Logger

    private val teamRooms = Locked(mutableMapOf<UUID, TeamRoom>())

    suspend fun getTeamRoomBySlug(teamSlug: String) = either {
        val team = teamStore.findBySlug(teamSlug).bind()

        getTeamRoom(team.id)
    }

    suspend fun getTeamRoom(teamId: UUID) = teamRooms.withLock {
        it.getOrPut(teamId) {
            val team = teamStore.findByIdInfallible(teamId)
            TeamRoom(team, channelService, messageService, authService)
        }
    }

    override suspend fun release() {
        logger.i { "Draining connections" }

        teamRooms.withLock { rooms ->
            rooms.values.forEach { it.close() }
            rooms.clear()
        }
    }
}
