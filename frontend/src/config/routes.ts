import { createRoute } from "@/lib/route";

export const navigationRoutes = {
  index: "/",
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
