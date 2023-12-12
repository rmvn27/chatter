import { z } from "zod";
import { nonEmptyString } from "./common";

export type UpdatePasswordForm = z.infer<typeof updatePasswordForm>;
export const updatePasswordForm = z.object({
  currentPassword: nonEmptyString(),
  newPassword: nonEmptyString(),
  newPasswordAgain: nonEmptyString(),
});

export type GeneralUserSettingsForm = z.infer<typeof generalUserSettingsForm>;
export const generalUserSettingsForm = z.object({
  name: nonEmptyString(),
});
