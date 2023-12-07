import { logOutCall } from "@/lib/fetch";
import { createLocalStorageSignal } from "@/lib/signals/localStorage";
import type { AuthTokens } from "@/models/auth";
import type { Accessor } from "solid-js";

export class TokenService {
  // HACK: the `TokenService` needs to be accessible for fetching
  // the api data so to not pass it everywhere it's easier to just
  // treat it as a singleton for this case
  private static instance: TokenService;

  static get = () => {
    if (TokenService.instance !== undefined) {
      return TokenService.instance;
    }

    TokenService.instance = new TokenService();
    return TokenService.instance;
  };

  private readonly tokens: Accessor<AuthTokens | undefined>;
  private readonly setToken: (newValue: AuthTokens | undefined) => void;

  private constructor() {
    const [tokens, setToken] = createLocalStorageSignal<AuthTokens>("app_token");
    this.tokens = tokens;
    this.setToken = setToken;
  }

  get accessToken(): string | undefined {
    return this.tokens()?.accessToken;
  }

  get refreshToken(): string | undefined {
    return this.tokens()?.refreshToken;
  }

  logOut = async () => {
    const currentTokens = this.tokens();
    this.setToken(undefined);

    // Try to remove the token from the db
    if (currentTokens !== undefined) {
      await logOutCall(currentTokens.refreshToken);
    }
  };

  authenticate = (tokens: AuthTokens) => {
    this.setToken(tokens);
  };
}
