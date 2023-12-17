package chatter.domain.services.teams

import chatter.TeamEntity
import chatter.lib.NatsService
import chatter.models.ChannelListChangedEvent
import chatter.models.ParticipantListChangedEvent
import chatter.models.TeamEvent
import java.util.UUID
import javax.inject.Inject

class TeamEventsService @Inject constructor(
    private val nats: NatsService
) {
    fun eventsForTeam(teamId: UUID) = nats.messages<TeamEvent>(eventsSubject(teamId))

    fun notifyParticipantsChanged(teams: List<TeamEntity>) {
        teams.forEach {
            nats.publish<TeamEvent>(
                eventsSubject(it.id),
                ParticipantListChangedEvent(it.slug)
            )
        }
    }

    fun notifyChannelsChanged(team: TeamEntity) {
        nats.publish<TeamEvent>(
            eventsSubject(team.id),
            ChannelListChangedEvent(team.slug)
        )
    }


    private fun eventsSubject(teamId: UUID) = "teamEvents.${teamId}"
}
