import { InviteSettingsCard } from "@/components/teamSettings/InviteSettingsCard";
import { TeamSettingsLayout } from "@/components/teamSettings/TeamSettingsLayout";
import { ChannelsSettingsCard } from "@/components/teamSettings/channels/ChannelSettingsCard";
import { GeneralSettingsCard } from "@/components/teamSettings/general/GeneralSettingsCard";
import { RouteComponent, RouteData } from "@/lib/route.types";
import { ChannelSettingsState } from "@/signals/teamSettings/channelSettingsState";
import { GeneralTeamSettingsState } from "@/signals/teamSettings/generalSettingsState";
import { InviteSettingsState } from "@/signals/teamSettings/inviteSettingsState";
import { z } from "zod";

export const routeData = {
  params: z.object({
    teamSlug: z.string(),
  }),
} satisfies RouteData;

export const Route: RouteComponent<typeof routeData> = (props) => {
  const teamSlug = () => props.params.teamSlug;
  const generalState = GeneralTeamSettingsState.create(teamSlug);
  const inviteState = InviteSettingsState.create(teamSlug);
  const channelsState = ChannelSettingsState.create(teamSlug);

  return (
    <TeamSettingsLayout>
      <GeneralSettingsCard state={generalState()} />
      <ChannelsSettingsCard state={channelsState()} />
      <InviteSettingsCard state={inviteState()} />
    </TeamSettingsLayout>
  );
};
