import { TeamLayout } from "@/components/team/TeamLayout";
import { ParticipantsSidebar } from "@/components/team/participants/ParticipantsSidebar";
import { TeamOverviewSidebar } from "@/components/team/sidebar/TeamSidebar";
import { RouteComponent, RouteData } from "@/lib/route.types";
import { TeamState } from "@/signals/teamState";
import { z } from "zod";

export const routeData = {
  params: z.object({
    teamSlug: z.string(),
    channelSlug: z.string().optional(),
  }),
} satisfies RouteData;

export const Route: RouteComponent<typeof routeData> = (props) => {
  const teamSlug = () => props.params.teamSlug;
  const channelSlug = () => props.params.channelSlug;
  const state = TeamState.create(teamSlug, channelSlug);

  const sidebar = <TeamOverviewSidebar state={state()} />;
  const participants = <ParticipantsSidebar state={state()} />;

  return <TeamLayout sidebar={sidebar} participants={participants} />;
};
