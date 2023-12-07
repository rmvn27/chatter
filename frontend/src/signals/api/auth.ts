import { apiPaths } from "@/config/api";
import { mutationKeys } from "@/config/query";
import { postJson } from "@/lib/fetch";
import { createBaseMutation, type MutationFn } from "@/lib/signals/query";
import { authTokens, type AuthForm, type AuthTokens } from "@/models/auth";

type AuthMutationProps = {
  type: "register" | "login";
};

export const authMutation: MutationFn<AuthForm, AuthTokens, AuthMutationProps> = ({
  type,
  onSuccess,
}) => {
  let mutationKey: string[];
  let apiPath: string;
  if (type === "register") {
    mutationKey = mutationKeys.auth.register;
    apiPath = apiPaths.auth.register;
  } else {
    mutationKey = mutationKeys.auth.login;
    apiPath = apiPaths.auth.login;
  }

  const mutationFn = (form: AuthForm) => postJson(apiPath, authTokens, form);

  return createBaseMutation<AuthForm, AuthTokens>({
    mutationKey,
    mutationFn,
    onSuccess,
  });
};
