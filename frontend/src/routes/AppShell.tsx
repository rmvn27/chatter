import { AddTeamModal } from "@/components/app/AddTeamModal";
import { AppSidebar } from "@/components/app/sidebar/AppSidebar";
import type { RouteComponent, RouteData } from "@/lib/route.types";
import { AppShellState } from "@/signals/app/appShellState";
import { createAuthGuard } from "@/signals/auth/authGuard";
import { Outlet } from "@solidjs/router";
import { createEffect } from "solid-js";
import { z } from "zod";

export const routeData = {
  guard: createAuthGuard("hasAccount"),
  params: z.object({
    teamSlug: z.string().optional(),
    channelSlug: z.string().optional(),
  }),
} satisfies RouteData;

export const Route: RouteComponent<typeof routeData> = (props) => {
  const routeData = () => props.params;
  const state = AppShellState.create(routeData);

  // looks weird but we have to create effects within effects
  // as the `state` could change when changing routes or any other dependency changes
  createEffect(() => {
    const s = state();
    s.notifyOnTeamAndChannelChange();
    s.syncQueryCache();
  });

  return (
    <>
      <div class="w-full h-full flex flex-row">
        <AppSidebar shell={state()} />

        <div class="w-full">
          <Outlet />
        </div>
      </div>

      <AddTeamModal opened={state().addTeamModalToggle} />
    </>
  );
};
