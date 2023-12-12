import { AppShell } from "@/components/app/AppShell";
import { AddTeamModal } from "@/components/app/addTeamModal/AddTeamModal";
import type { RouteComponent, RouteData } from "@/lib/route.types";
import { AppShellState } from "@/signals/app/appShellState";
import { createAuthGuard } from "@/signals/auth/authGuard";
import { z } from "zod";

export const routeData = {
  guard: createAuthGuard("hasAccount"),
  params: z.object({
    teamSlug: z.string().optional(),
  }),
} satisfies RouteData;

export const Route: RouteComponent<typeof routeData> = (props) => {
  const teamSlug = () => props.params.teamSlug;
  const state = AppShellState.create(teamSlug);

  return (
    <>
      <AppShell shellState={state()} />

      <AddTeamModal opened={state().addTeamModalToggle} />
    </>
  );
};
