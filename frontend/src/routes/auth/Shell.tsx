import { AuthLayout } from "@/components/auth/AuthLayout";
import type { RouteComponent, RouteData } from "@/lib/route.types";
import { createAuthGuard } from "@/signals/auth/authGuard";
import { Outlet } from "@solidjs/router";

export const routeData = {
  guard: createAuthGuard("noAccount"),
} satisfies RouteData;

export const Route: RouteComponent<typeof routeData> = () => {
  return (
    <AuthLayout>
      <Outlet />
    </AuthLayout>
  );
};
