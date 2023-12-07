import { RegisterCard } from "@/components/auth/AuthCards";
import type { RouteComponent, RouteData } from "@/lib/route.types";
import { AuthState } from "@/signals/auth/authState";

export const routeData = {} satisfies RouteData;

export const Route: RouteComponent<typeof routeData> = () => {
  const state = AuthState.create("register");

  return <RegisterCard error={state().error} onSubmit={state().mutate} />;
};
