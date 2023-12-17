package chatter.domain.services.teams

import chatter.TeamEntity
import chatter.lib.NatsService
import chatter.lib.log.getValue
import chatter.models.ChannelListChangedEvent
import chatter.models.ParticipantListChangedEvent
import chatter.models.TeamEvent
import co.touchlab.kermit.Logger
import java.util.UUID
import javax.inject.Inject

class TeamEventsService @Inject constructor(
    private val nats: NatsService
) {
    private val logger by Logger

    fun eventsForTeam(teamId: UUID) = nats.messages<TeamEvent>(eventsSubject(teamId))

    fun notifyParticipantsChanged(teams: List<TeamEntity>) {
        teams.forEach {
            val subject = eventsSubject(it.id)
            nats.publish<TeamEvent>(
                subject,
                ParticipantListChangedEvent(it.slug)
            )

            logger.d { "Pushed TeamEvent(ParticipantChange) to $subject" }
        }
    }

    fun notifyChannelsChanged(team: TeamEntity) {
        val subject = eventsSubject(team.id)
        nats.publish<TeamEvent>(
            subject,
            ChannelListChangedEvent(team.slug)
        )

        logger.d { "Pushed TeamEvent(ChannelChange) to $subject" }
    }


    private fun eventsSubject(teamId: UUID) = "teamEvents.${teamId}"
}
