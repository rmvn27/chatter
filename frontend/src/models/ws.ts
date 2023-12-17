import { z } from "zod";
import { message } from "./messages";

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

const messageRecievedEvent = z.object({
  type: z.literal("messageReceived"),
  message,
});

const channelListChangedEvent = z.object({
  type: z.literal("channelListChanged"),
  teamSlug: z.string(),
});

const participantListChangedEvent = z.object({
  type: z.literal("participantListChanged"),
  teamSlug: z.string(),
});

export type WsEvent = z.infer<typeof wsEvent>;
export const wsEvent = z.discriminatedUnion("type", [
  errorEvent,
  messageRecievedEvent,
  channelListChangedEvent,
  participantListChangedEvent,
]);
