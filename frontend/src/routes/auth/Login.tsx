import { LoginCard } from "@/components/auth/AuthCards.jsx";
import type { RouteComponent, RouteData } from "@/lib/route.types";
import { AuthState } from "@/signals/auth/authState";

export const routeData = {} satisfies RouteData;

export const Route: RouteComponent<typeof routeData> = () => {
  const state = AuthState.create("login");

  return <LoginCard error={state().error} onSubmit={state().mutate} />;
};
