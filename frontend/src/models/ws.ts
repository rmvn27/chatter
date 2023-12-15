import { z } from "zod";

const enterTeamCommand = z.object({
  type: z.literal("enterTeam"),
  teamSlug: z.string(),
});

const enterChannelCommand = z.object({
  type: z.literal("enterChannel"),
  channelSlug: z.string(),
});

const enterTeamAndChannelCommand = z.object({
  type: z.literal("enterTeamAndChannel"),
  teamSlug: z.string(),
  channelSlug: z.string(),
});

const leaveTeamCommand = z.object({ type: z.literal("leaveTeam") });
const leaveChannelCommand = z.object({ type: z.literal("leaveChannel") });

const authenticateCommand = z.object({
  type: z.literal("authenticate"),
  token: z.string(),
});

const sendMessageCommand = z.object({
  type: z.literal("sendTextMessage"),
  message: z.string(),
});

export type WsCommand = z.infer<typeof wsCommand>;
export const wsCommand = z.discriminatedUnion("type", [
  enterTeamCommand,
  enterChannelCommand,
  enterTeamAndChannelCommand,
  leaveTeamCommand,
  leaveChannelCommand,
  authenticateCommand,
  sendMessageCommand,
]);

const errorEvent = z.object({
  type: z.literal("wsError"),
  message: z.string(),
  code: z.number(),
});

export type MessageEvent = z.infer<typeof messageEvent>;
const messageEvent = z.object({
  type: z.literal("message"),
  content: z.string(),

  userId: z.string().optional(),
  timestamp: z.number(),

  messageType: z.literal("text").optional(),
});

export type WsEvent = z.infer<typeof wsEvent>;
export const wsEvent = z.discriminatedUnion("type", [errorEvent, messageEvent]);
