import { z } from "zod";
import { nonEmptyString } from "./common";

export type TeamChannel = z.infer<typeof teamChannel>;
export const teamChannel = z.object({
  id: z.string(),
  name: z.string(),
  slug: z.string(),
});

export type CreateChannelForm = z.infer<typeof createChannelForm>;
export const createChannelForm = z.object({
  name: nonEmptyString(),
});

export type UpdateChannelForm = z.infer<typeof createChannelForm>;
export const updateChannelForm = z.object({
  name: nonEmptyString(),
});
