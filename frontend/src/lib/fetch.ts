import { apiPaths } from "@/config/api";
import { authTokens } from "@/models/auth";
import { TokenService } from "@/services/tokenService";
import { z } from "zod";

export class ApplicationError extends Error {
  constructor(
    override message: string,
    public code: number,
  ) {
    super(message);
  }
}

export const postJson = <Schema extends z.ZodTypeAny>(
  path: string,
  responseSchema: Schema,
  body?: unknown,
) => fetchJson(path, responseSchema, "POST", body);

export const patchJson = <Schema extends z.ZodTypeAny>(
  path: string,
  responseSchema: Schema,
  body?: unknown,
) => fetchJson(path, responseSchema, "PATCH", body);

export const getJson = <Schema extends z.ZodTypeAny>(
  path: string,
  responseSchema: Schema,
) => fetchJson(path, responseSchema, "GET");

export const deleteJson = <Schema extends z.ZodTypeAny>(
  path: string,
  responseSchema: Schema,
) => fetchJson(path, responseSchema, "DELETE");

// Try the first fetch, when it fails try to authenticate again and redo the request
//
// Just fail on the second time or when the token refetching fails
const fetchJson = async <Schema extends z.ZodTypeAny>(
  path: string,
  responseSchema: Schema,
  method: string,
  body?: unknown,
): Promise<z.infer<Schema>> => {
  const tokenService = TokenService.get();

  // Since accessToken in the service is a getter, the newest value will be used
  const fetcher = () =>
    baseFetchJson(path, responseSchema, method, body, tokenService.accessToken);

  try {
    return await fetcher();
  } catch (e) {
    const refreshToken = tokenService.refreshToken;

    // Check for application error to meka the next check simpler
    if (!(e instanceof ApplicationError)) {
      throw e;
    } else if (e.code === 401 && refreshToken !== undefined) {
      // Try to refetch, when something fails just log out
      try {
        const newTokens = await fetchNewTokens(refreshToken);
        tokenService.authenticate(newTokens);

        return await fetcher();
      } catch (e) {
        await tokenService.logOut();
        throw e;
      }
    } else {
      throw e;
    }
  }
};

const fetchNewTokens = (refreshToken: string) =>
  baseFetchJson(
    apiPaths.auth.tokens,
    authTokens,
    "POST",
    { refresh_token: refreshToken },
    undefined,
  );

export const logOutCall = (refreshToken: string) =>
  baseFetchJson(
    apiPaths.auth.logout,
    z.unknown(),
    "POST",
    { refresh_token: refreshToken },
    undefined,
  );

// Simple fetch wrapper that adds an token when supplied
// and parses the zod schema
const baseFetchJson = async <Schema extends z.ZodTypeAny>(
  path: string,
  responseSchema: Schema,
  method: string,
  body: unknown | undefined,
  accessToken: string | undefined,
): Promise<z.infer<Schema>> => {
  const rawBody = body ? JSON.stringify(body) : undefined;
  const headers = new Headers({
    "Content-Type": "application/json",
  });

  if (accessToken !== undefined) {
    headers.append("Authorization", `Bearer ${accessToken}`);
  }

  const response = await fetch(`/api${path}`, {
    body: rawBody,
    method,
    headers,
  });
  const responseBody = await response.json();

  if (!response.ok) {
    throw new ApplicationError(responseBody.message, response.status);
  }

  return await responseSchema.parseAsync(responseBody);
};
