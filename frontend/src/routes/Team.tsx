import { TeamShell } from "@/components/team/TeamShell";
import { RouteComponent, RouteData } from "@/lib/route.types";
import { TeamState } from "@/signals/team/teamState";
import { z } from "zod";

export const routeData = {
  params: z.object({
    teamSlug: z.string(),
  }),
} satisfies RouteData;

export const Route: RouteComponent<typeof routeData> = (props) => {
  const teamSlug = () => props.params.teamSlug;
  const state = TeamState.create(teamSlug);

  return <TeamShell state={state()} />;
};
