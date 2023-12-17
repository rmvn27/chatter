package chatter.domain.services.teams

import arrow.core.raise.either
import chatter.MessageEntity
import chatter.db.TeamMessageQueries
import chatter.db.asList
import chatter.db.insert
import chatter.lib.NatsService
import chatter.models.Message
import chatter.models.MessageContent
import chatter.models.UserPrincipal
import java.util.UUID
import javax.inject.Inject

class MessageService @Inject constructor(
    private val nats: NatsService,
    private val queries: TeamMessageQueries,
    private val channelService: ChannelService
) {
    suspend fun findByTimestamp(teamSlug: String, channelSlug: String, timestamp: Long, maybePageSize: Int?) = either {
        val pageSize = maybePageSize ?: 50
        val channel = channelService.findChannelBySlugs(teamSlug = teamSlug, channelSlug = channelSlug).bind()

        queries.findByTimestamp(
            channelId = channel.id,
            timestamp = timestamp,
            limit = pageSize.toLong()
        ).asList().map {
            Message(
                userId = it.userId,
                content = MessageContent.Text(it.content),
                timestamp = it.timestamp
            )
        }
    }

    fun liveMessages(
        teamId: UUID,
        channelId: UUID
    ) = nats.messages<Message>(Message.eventSubject(teamId, channelId))

    suspend fun sendMessage(teamId: UUID, channelId: UUID, user: UserPrincipal, msg: String) {
        val now = System.currentTimeMillis()

        val message = Message(
            content = MessageContent.Text(msg),
            timestamp = now,
            userId = user.userId
        )

        MessageEntity(
            id = UUID.randomUUID(),
            channelId = channelId,
            userId = user.userId,

            content = msg,
            type = "text", // for now we only send text messages
            timestamp = now,
        ).insert(queries::create)

        nats.publish(messagesSubject(teamId, channelId), message)
    }

    private fun messagesSubject(teamId: UUID, channelId: UUID) = "liveMessages.${teamId}.${channelId}"
}
