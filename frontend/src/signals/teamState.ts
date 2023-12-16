import { navigationRoutes } from "@/config/routes";
import { Channel } from "@/models/channels";
import { Participant, Team } from "@/models/teams";
import { Navigator, useNavigate } from "@solidjs/router";
import { Accessor, createMemo } from "solid-js";
import { channelsQuery } from "./api/channels";
import { participantsQuery, removeParticipant } from "./api/participants";
import { leaveTeamMutation, teamQuery } from "./api/teams";

export class TeamState {
  static create = (
    teamSlug: Accessor<string>,
    channelSlug: Accessor<string | undefined>,
  ) => {
    const nav = useNavigate();

    return createMemo(() => new TeamState(teamSlug(), nav, channelSlug));
  };

  private readonly teamQuery;
  private readonly participantsQuery;

  private readonly removeParticipantMutation;
  private readonly leaveMutation;

  private readonly channelsQueries;

  readonly channelSlug: Accessor<string | undefined>;
  private constructor(
    private readonly teamSlug: string,
    private readonly nav: Navigator,
    channelSlug: Accessor<string | undefined>,
  ) {
    this.teamQuery = teamQuery({ teamSlug });
    this.participantsQuery = participantsQuery({ teamSlug });

    this.removeParticipantMutation = removeParticipant({ teamSlug });

    this.leaveMutation = leaveTeamMutation({
      teamSlug,
      onSuccess: () => nav(navigationRoutes.index),
    });

    this.channelsQueries = channelsQuery({ teamSlug });
    this.channelSlug = createMemo(() => channelSlug());
  }

  get team(): Team | undefined {
    return this.teamQuery.data;
  }

  get participants(): Participant[] {
    return this.participantsQuery.data ?? [];
  }

  get channels(): Channel[] {
    return this.channelsQueries.data ?? [];
  }

  removeParticipant = (participant: Participant) =>
    this.removeParticipantMutation.mutate(participant);

  navToSettings = () => {
    this.nav(navigationRoutes.team(this.teamSlug).settings);
  };

  navToMessages = (channelSlug: string) => {
    this.nav(navigationRoutes.team(this.teamSlug).messages(channelSlug));
  };

  leave = async () => this.leaveMutation.mutate(undefined);
}
