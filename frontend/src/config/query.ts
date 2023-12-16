export const queryKeys = {
  allTeams: () => ["teams"],
  team: (slug: string) => () => ["teams", slug],

  participants: (slug: string) => () => ["teams", slug, "participants"],
  invites: (slug: string) => () => ["teams", slug, "invites"],
  channels: (slug: string) => () => ["teams", slug, "channels"],

  messages: (teamSlug: string, channelSlug: string) => () => [
    "teams",
    teamSlug,
    "channels",
    channelSlug,
    "messages",
  ],
};

export const mutationKeys = {
  auth: {
    register: ["auth", "register"],
    login: ["auth", "login"],
  },
  teams: {
    create: ["teams", "create"],
    update: ["teams", "update"],
    delete: ["teams", "delete"],

    join: ["teams", "join"],
    leave: ["teams", "leave"],
  },
  participants: {
    delete: ["participants", "delete"],
  },
  invites: {
    create: ["invites", "create"],
    delete: ["invites", "delete"],
  },
  user: {
    updatePassword: ["user", "updatePassword"],
    updateGeneral: ["user", "updateGeneral"],
    delete: ["user", "delete"],
  },
  channels: {
    create: ["channels", "create"],
    update: ["channels", "update"],
    delete: ["channels", "delete"],
  },
};
