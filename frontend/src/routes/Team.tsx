import { TeamShell } from "@/components/team/TeamShell";
import { RouteComponent, RouteData } from "@/lib/route.types";
import { TeamState } from "@/signals/team/teamState";
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

  return <TeamShell state={state()} />;
};
