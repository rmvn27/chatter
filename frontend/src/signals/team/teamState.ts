import { navigationRoutes } from "@/config/routes";
import { Team, TeamParticipant } from "@/models/teams";
import { Navigator, useNavigate } from "@solidjs/router";
import { Accessor, createMemo } from "solid-js";
import { participantsQuery, removeParticipant } from "../api/participants";
import { leaveTeamMutation, teamQuery } from "../api/teams";

export class TeamState {
  static create = (teamSlug: Accessor<string>) => {
    const nav = useNavigate();

    return createMemo(() => new TeamState(teamSlug(), nav));
  };

  private teamQuery;
  private participantsQuery;

  private removeParticipantMutation;
  private leaveMutation;

  private constructor(
    private teamSlug: string,
    private nav: Navigator,
  ) {
    this.teamQuery = teamQuery({ teamSlug });
    this.participantsQuery = participantsQuery({ teamSlug });

    this.removeParticipantMutation = removeParticipant({ teamSlug });

    this.leaveMutation = leaveTeamMutation({
      teamSlug,
      onSuccess: () => nav(navigationRoutes.index),
    });
  }

  get team(): Team | undefined {
    return this.teamQuery.data;
  }

  get participants(): TeamParticipant[] {
    return this.participantsQuery.data ?? [];
  }

  removeParticipant = (participant: TeamParticipant) =>
    this.removeParticipantMutation.mutate(participant);

  navToSettings = () => {
    this.nav(navigationRoutes.team(this.teamSlug).settings);
  };

  leave = async () => this.leaveMutation.mutate(undefined);
}
