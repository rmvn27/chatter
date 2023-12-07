import { z } from "zod";
import { nonEmptyString } from "./common";

export type AuthTokens = z.infer<typeof authTokens>;
export const authTokens = z.object({
  accessToken: z.string(),
  refreshToken: z.string(),
});

export type AuthForm = z.infer<typeof authForm>;
export const authForm = z.object({
  username: nonEmptyString(),
  password: nonEmptyString(),
});
