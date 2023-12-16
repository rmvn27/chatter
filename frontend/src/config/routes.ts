import { createRoute } from "@/lib/route";

export const navigationRoutes = {
  index: "/",
  team: (teamSlug: string) => ({
    base: `/teams/${teamSlug}`,
    messages: (channelSlug: string) => `/teams/${teamSlug}/messages/${channelSlug}`,
    settings: `/teams/${teamSlug}/settings`,
  }),
  login: "/auth/login",
  register: "/auth/register",
  settings: "/settings",
};

export const appRoutes = () => {
  const teamRoute = createRoute({
    path: "/teams/:teamSlug",
    component: () => import("../routes/Team"),
    children: [
      createRoute({ path: "/" }),
      createRoute({
        path: "/settings",
        component: () => import("../routes/TeamSettings"),
      }),
      createRoute({
        path: "/messages/:channelSlug",
        component: () => import("../routes/Messages"),
      }),
    ],
  });

  const appRoute = createRoute({
    path: "/",
    component: () => import("../routes/AppShell"),
    children: [
      // create a empty route so we actually render '/'
      createRoute({ path: "/" }),
      // settings
      createRoute({
        path: "/settings",
        component: () => import("../routes/Settings"),
      }),
      // team
      teamRoute,
    ],
  });

  const authRoute = createRoute({
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
  });

  return createRoute({
    path: "/",
    children: [
      // app
      appRoute,
      // auth
      authRoute,
    ],
  });
};
