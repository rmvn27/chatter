import { TeamSettings } from "@/components/teamSettings/TeamSettings";
import { RouteComponent, RouteData } from "@/lib/route.types";
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

  return <TeamSettings generalState={generalState()} inviteState={inviteState()} />;
};
