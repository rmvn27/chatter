import type { RouteComponent, RouteData } from "@/lib/route.types";
import { createAuthGuard } from "@/signals/auth/authGuard";

export const routeData = {
  guard: createAuthGuard("hasAccount"),
} satisfies RouteData;

export const Route: RouteComponent<typeof routeData> = () => {
  return <h1 class="text-white-300 text-xl">HELLO WORLD</h1>;
};
