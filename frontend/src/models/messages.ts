import { z } from "zod";

export type DisplayTeamMessage = {
  username: string;
  date: string;

  content: string;
};

const messageContent = z
  .object({
    content: z.string(),
    type: z.literal("text"),
  })
  .transform((d) => d.content);

export type TeamMessage = z.infer<typeof teamMessage>;
export const teamMessage = z.object({
  content: messageContent,

  userId: z.string().optional(),
  timestamp: z.number(),
});

export type SendMessageForm = z.infer<typeof sendMessageForm>;
export const sendMessageForm = z.object({
  message: z.string(),
});
