import { z } from "zod";

export type DisplayTeamMessage = {
  username: string;
  date: string;

  content: string;
};

export type TeamMessage = z.infer<typeof teamMessage>;
export const teamMessage = z.object({
  content: z.string(),
  contentType: z.literal("text").optional(),

  userId: z.string().optional(),
  timestamp: z.number(),
});

export type SendMessageForm = z.infer<typeof sendMessageForm>;
export const sendMessageForm = z.object({
  message: z.string(),
});
