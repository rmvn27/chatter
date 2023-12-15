package chatter.domain.services

import arrow.core.raise.either
import chatter.TeamChannelMessageEntity
import chatter.db.TeamMessageQueries
import chatter.db.asList
import chatter.db.insert
import chatter.lib.NatsService
import chatter.lib.toUUID
import chatter.models.ChannelMessage
import chatter.models.MessageEvent
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject

class TeamMessageService @Inject constructor(
    private val nats: NatsService,
    private val queries: TeamMessageQueries,
    private val channelService: TeamChannelService
) {
    suspend fun findByTimestamp(teamSlug: String, channelSlug: String, timestamp: Long, maybePageSize: Int?) = either {
        val pageSize = maybePageSize ?: 50
        val channel = channelService.findChannelBySlug(teamSlug = teamSlug, channelSlug = channelSlug).bind()

        queries.findByTimestamp(
            channelId = channel.id,
            timestamp = timestamp,
            limit = pageSize.toLong()
        ).asList().map {
            ChannelMessage(
                userId = it.userId,
                content = it.content,
                contentType = it.type,
                timestamp = it.timestamp
            )
        }
    }

    // subscribe the liveMessages for a whole team. We subscribe for the complete
    // team since otherwise we would create a dispatcher (this means also a thread)
    // for every channel which does not scale well
    fun liveMessages(
        teamId: UUID
    ) = nats.messages<ChannelMessage>(receiveSubject(teamId)).map {
        val splitSubject = it.subject.split(".")

        MessageEvent(
            teamId = splitSubject[1].toUUID(),
            channelId = splitSubject[2].toUUID(),

            message = it.data.content,

            timestamp = it.data.timestamp,
            userId = it.data.userId
        )
    }

    suspend fun sendMessage(teamId: UUID, channelId: UUID, userId: UUID, msg: String) {
        val now = System.currentTimeMillis()

        val message = ChannelMessage(
            content = msg,
            timestamp = now,
            userId = userId
        )

        TeamChannelMessageEntity(
            id = UUID.randomUUID(),
            channelId = channelId,
            userId = userId,

            content = msg,
            type = "text",
            timestamp = now,
        ).insert(queries::create)

        nats.publish(sendSubject(teamId, channelId), message)
    }

    private fun receiveSubject(teamId: UUID) = "liveMessages.${teamId}.*"
    private fun sendSubject(teamId: UUID, channelId: UUID) = "liveMessages.${teamId}.${channelId}"
}
