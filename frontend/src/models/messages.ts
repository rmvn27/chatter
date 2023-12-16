import { z } from "zod";

export type PrettifiedMessage = {
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

export type Message = z.infer<typeof message>;
export const message = z.object({
  content: messageContent,

  userId: z.string().optional(),
  timestamp: z.number(),
});

export type SendMessageForm = z.infer<typeof sendMessageForm>;
export const sendMessageForm = z.object({
  message: z.string(),
});
