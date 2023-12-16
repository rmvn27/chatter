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
    bySlug: (slug: string) => `/teams/${slug}`,
    join: (slug: string) => `/teams/${slug}/join`,
    leave: (slug: string) => `/teams/${slug}/leave`,
  },
  participants: (teamSlug: string) => ({
    base: `/teams/${teamSlug}/participants`,
    withId: (id: string) => `/teams/${teamSlug}/participants/${id}`,
  }),
  invites: (teamSlug: string) => ({
    base: `/teams/${teamSlug}/invites`,
    withId: (id: string) => `/teams/${teamSlug}/invites/${id}`,
  }),
  channels: (teamSlug: string) => ({
    base: `/teams/${teamSlug}/channels`,
    withSlug: (channelSlug: string) => `/teams/${teamSlug}/channels/${channelSlug}`,
  }),
  messages: (teamSlug: string, channelSlug: string) =>
    `/teams/${teamSlug}/channels/${channelSlug}/messages`,
};
