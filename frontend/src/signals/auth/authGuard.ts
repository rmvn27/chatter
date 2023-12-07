import { navigationRoutes } from "@/config/routes";
import type { RouteGuard } from "@/lib/route.types";
import { useServices } from "../services";

export const createAuthGuard = (
  targetState: "hasAccount" | "noAccount",
): RouteGuard => {
  return () => {
    const services = useServices();

    return () => {
      const hasToken = services.token.accessToken !== undefined;
      if (targetState === "hasAccount") {
        if (!hasToken) {
          return { type: "replace", to: navigationRoutes.login };
        }
      } else {
        if (hasToken) {
          return { type: "replace", to: navigationRoutes.index };
        }
      }

      return { type: "keep" };
    };
  };
};
