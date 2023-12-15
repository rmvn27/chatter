import { logOutCall } from "@/lib/fetch";
import { createLocalStorageSignal } from "@/lib/signals/localStorage";
import type { AuthTokens } from "@/models/auth";
import type { Accessor } from "solid-js";
import { WebsocketService } from "./websocketService";

// manage persistance and access for the auth tokens in the application
export class TokenService {
  // HACK: the `TokenService` needs to be accessible for fetching
  // the api data so to not pass it everywhere it's easier to just
  // treat it as a singleton for this case
  private static instance: TokenService | undefined = undefined;

  static get = () => {
    if (TokenService.instance === undefined) {
      throw new Error("TokenService was not initialized!");
    }

    return TokenService.instance;
  };
  static set = (i: TokenService) => {
    TokenService.instance = i;
  };

  private readonly tokens: Accessor<AuthTokens | undefined>;
  private readonly setToken: (newValue: AuthTokens | undefined) => void;

  constructor(private readonly wsService: WebsocketService) {
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
    await this.wsService.disconnect();

    // Try to remove the token from the db
    if (currentTokens !== undefined) {
      await logOutCall(currentTokens.refreshToken);
    }
  };

  // setup tokens for other services
  // as of now only the websocket service needs it for it's connection
  setupTokens = () => {
    if (this.accessToken != undefined) {
      this.wsService.connect(this.accessToken);
    }
  };

  authenticate = (tokens: AuthTokens) => {
    // reset the ws connection if we already had tokens saved
    if (this.accessToken !== undefined) {
      this.wsService.disconnect();
    }

    this.wsService.connect(tokens.accessToken);

    this.setToken(tokens);
  };
}
