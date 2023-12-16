import { navigationRoutes } from "@/config/routes";
import { createZodForm } from "@/lib/signals/form";
import { createToggle } from "@/lib/signals/toggle";
import {
  GeneralUserSettingsForm,
  UpdatePasswordForm,
  generalUserSettingsForm,
  updatePasswordForm,
} from "@/models/user";
import { TokenService } from "@/services/tokenService";
import { reset } from "@modular-forms/solid";
import { useNavigate, type Navigator } from "@solidjs/router";
import { useQueryClient, type QueryClient } from "@tanstack/solid-query";
import { createMemo, type Accessor } from "solid-js";
import {
  deleteUserMutation,
  updateGeneralUserDataMutation,
  updatePasswordMutation,
} from "./api/user";
import { useServices } from "./services";

export class UserSettingsState {
  static create = (): Accessor<UserSettingsState> => {
    const query = useQueryClient();
    const services = useServices();
    const nav = useNavigate();

    return createMemo(() => new UserSettingsState(query, services.token, nav));
  };

  readonly generalSettingsForm = createZodForm(generalUserSettingsForm);
  readonly updatePasswordForm = createZodForm(updatePasswordForm);
  readonly deleteToggle = createToggle();

  private readonly deleteMutation;
  private readonly updatePasswordMutation;
  private readonly updateGeneralUserDataMutation;

  constructor(client: QueryClient, tokenService: TokenService, navigate: Navigator) {
    this.deleteMutation = deleteUserMutation({
      onSuccess: async () => {
        client.clear();
        await tokenService.logOut();
        navigate(navigationRoutes.login, { replace: true });
      },
    });

    this.updatePasswordMutation = updatePasswordMutation({
      onSuccess: () => {
        reset(this.updatePasswordForm);
      },
    });

    this.updateGeneralUserDataMutation = updateGeneralUserDataMutation({
      onSuccess: () => {
        reset(this.generalSettingsForm);

        // invalidate all participants
        // since we might have updated the name of the user
        client.invalidateQueries({
          predicate: (query) => {
            return (
              query.queryKey[0] === "teams" && query.queryKey[2] === "participants"
            );
          },
        });
      },
    });
  }

  // expose the error since we can't associate
  // 'not same' password error to a specific field
  get updateError(): string | undefined {
    return this.updatePasswordMutation.error?.message;
  }

  updatePassword = (form: UpdatePasswordForm) => {
    this.updatePasswordMutation.mutate(form);
  };

  updateGeneralUserData = (form: GeneralUserSettingsForm) => {
    this.updateGeneralUserDataMutation.mutate(form);
  };

  deleteAccount = () => this.deleteMutation.mutate(undefined);
}
