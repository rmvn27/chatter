export const queryKeys = {
  teams: {
    all: () => ["teams"],
    bySlug: (slug: string) => ({
      base: () => ["teams", slug],
      participants: () => ["teams", slug, "participants"],
      invites: () => ["teams", slug, "invites"],
      channels: () => ["teams", slug, "channels"],
    }),
  },
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
