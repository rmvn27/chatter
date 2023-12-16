import { apiPaths } from "@/config/api";
import { mutationKeys } from "@/config/query";
import { ApplicationError, deleteJson, patchJson } from "@/lib/fetch";
import { MutationFn, createBaseMutation } from "@/lib/signals/query";
import { GeneralUserSettingsForm, UpdatePasswordForm } from "@/models/user";
import { z } from "zod";

export const updatePasswordMutation: MutationFn<UpdatePasswordForm> = ({
  onSuccess,
}) => {
  const mutationFn = async (data: UpdatePasswordForm) => {
    if (data.newPassword !== data.newPasswordAgain) {
      throw new ApplicationError("Passwords don't match", 400);
    }

    await patchJson(apiPaths.user, z.unknown(), {
      password: data.newPassword,
    });
  };

  return createBaseMutation({
    mutationKey: mutationKeys.user.updatePassword,
    mutationFn,
    onSuccess,
  });
};

export const updateGeneralUserDataMutation: MutationFn<
  GeneralUserSettingsForm,
  unknown
> = ({ onSuccess }) => {
  const mutationFn = (data: GeneralUserSettingsForm) =>
    patchJson(apiPaths.user, z.unknown(), data);

  return createBaseMutation({
    mutationKey: mutationKeys.user.updateGeneral,
    mutationFn,
    onSuccess,
  });
};

export const deleteUserMutation: MutationFn<undefined, unknown> = ({ onSuccess }) => {
  const mutationFn = () => deleteJson(apiPaths.user, z.unknown());

  return createBaseMutation({
    mutationKey: mutationKeys.user.delete,
    mutationFn,
    onSuccess: onSuccess,
  });
};
