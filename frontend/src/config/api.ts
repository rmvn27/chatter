export const apiPaths = {
  auth: {
    login: "/auth/login",
    register: "/auth/register",
    logout: "/auth/logout",
    tokens: "/auth/tokens",
  },
  user: "/user",
  teams: {
    base: "/teams",
    bySlug: (slug: string) => ({
      details: `/teams/${slug}`,
      join: `/teams/${slug}/join`,
      leave: `/teams/${slug}/leave`,
      participants: {
        base: `/teams/${slug}/participants`,
        withId: (id: string) => `/teams/${slug}/participants/${id}`,
      },
      invites: {
        base: `/teams/${slug}/invites`,
        withId: (id: string) => `/teams/${slug}/invites/${id}`,
      },
      channels: {
        base: `/teams/${slug}/channels`,
        withSlug: (channelSlug: string) => `/teams/${slug}/channels/${channelSlug}`,
        messages: (channelSlug: string) =>
          `/teams/${slug}/channels/${channelSlug}/messages`,
      },
    }),
  },
};
