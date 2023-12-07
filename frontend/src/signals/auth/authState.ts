import { navigationRoutes } from "@/config/routes";
import type { AuthForm, AuthTokens } from "@/models/auth";
import { TokenService } from "@/services/tokenService";
import { useNavigate, type Navigator } from "@solidjs/router";
import { createMemo, type Accessor } from "solid-js";
import { authMutation } from "../api/auth";
import { useServices } from "../services";

export class AuthState {
  private mutation: ReturnType<typeof authMutation>;

  static create = (type: "login" | "register"): Accessor<AuthState> => {
    const nav = useNavigate();
    const services = useServices();
    return createMemo(() => new AuthState(type, nav, services.token));
  };

  constructor(
    type: "login" | "register",
    navigate: Navigator,
    tokenStorage: TokenService,
  ) {
    this.mutation = authMutation({
      type,
      onSuccess: (tokens: AuthTokens) => {
        tokenStorage.authenticate(tokens);
        navigate(navigationRoutes.index);
      },
    });
  }

  get error(): string | undefined {
    return this.mutation?.error?.message;
  }

  mutate = (data: AuthForm) => this.mutation.mutate(data);
}
