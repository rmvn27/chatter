import { createRoute } from "@/lib/route";

export const navigationRoutes = {
  index: "/",
  team: (teamSlug: string) => ({
    base: `/teams/${teamSlug}`,
    settings: `/teams/${teamSlug}/settings`,
  }),
  login: "/auth/login",
  register: "/auth/register",
};

export const appRoutes = createRoute({
  path: "/",
  children: [
    // app
    createRoute({
      path: "/",
      component: () => import("../routes/AppShell"),
      children: [
        // create a empty route so we actually render '/'
        createRoute({ path: "/" }),
        // team
        createRoute({
          path: "/teams/:teamSlug",
          component: () => import("../routes/Team"),
          children: [
            createRoute({ path: "/" }),
            createRoute({
              path: "/settings",
              component: () => import("../routes/TeamSettings"),
            }),
          ],
        }),
      ],
    }),
    // auth
    createRoute({
      path: "/auth",
      component: () => import("../routes/auth/Shell"),
      children: [
        createRoute({
          path: "/login",
          component: () => import("../routes/auth/Login"),
        }),
        createRoute({
          path: "/register",
          component: () => import("../routes/auth/Register"),
        }),
      ],
    }),
  ],
});
