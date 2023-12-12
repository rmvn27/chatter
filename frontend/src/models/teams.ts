import { z } from "zod";
import { nonEmptyString } from "./common";

export type TeamColor = "red" | "green" | "teal" | "blue" | "purple";

export const teamColors: TeamColor[] = ["red", "green", "teal", "blue", "purple"];

export type Team = z.infer<typeof team>;
export const team = z.object({
  id: z.string(),
  name: z.string(),
  slug: z.string(),

  isOwner: z.boolean(),
});

export type TeamParticipant = z.infer<typeof teamParticipant>;
export const teamParticipant = z.object({
  id: z.string(),
  username: z.string(),

  teamOwner: z.boolean(),
});

export type TeamInvite = string;
export const teamInvite = z.string();

export type CreateTeamForm = z.infer<typeof createTeamForm>;
export const createTeamForm = z.object({
  name: nonEmptyString(),
});

export type JoinTeamForm = z.infer<typeof joinTeamForm>;
export const joinTeamForm = z.object({
  invite: nonEmptyString(),
});

export type UpdateTeamForm = z.infer<typeof updateTeamForm>;
export const updateTeamForm = z.object({
  name: nonEmptyString(),
});
