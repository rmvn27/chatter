import type { ApplicationError } from "@/lib/fetch";
import type { MaybePromise } from "@/lib/types";
import {
  createMutation,
  createQuery,
  useQueryClient,
  type CreateMutationOptions,
  type CreateMutationResult,
  type CreateQueryOptions,
  type CreateQueryResult,
} from "@tanstack/solid-query";

export type InvalidationFn = () => Promise<void>;

export const createQueryInvalidation = (
  key: string[] | (() => string[]),
): InvalidationFn => {
  const client = useQueryClient();

  return async () => {
    const actualKey = typeof key === "function" ? key() : key;

    return await client.invalidateQueries({ queryKey: actualKey });
  };
};

type BaseInput<Output> = {
  onSuccess?: (data: Output) => MaybePromise<void>;
};

export type MutationFn<
  MutationInput = unknown,
  MutationResult = void,
  Input extends object = Record<string, unknown>,
> = (
  data: Input & BaseInput<MutationResult>,
) => CreateMutationResult<MutationResult, ApplicationError, MutationInput>;

// Simple wrapper that improves the usage of generics
export const createBaseMutation = <MutationInput = unknown, MutationResult = void>(
  options: CreateMutationOptions<MutationResult, ApplicationError, MutationInput>,
) => createMutation<MutationResult, ApplicationError, MutationInput>(options);

export type QueryFn<
  QueryResult = void,
  Input extends object = Record<string, unknown>,
> = (
  data: Input & BaseInput<QueryResult>,
) => CreateQueryResult<QueryResult, ApplicationError>;

// Simple wrapper that improves the usage of generics
export const createBaseQuery = <QueryResult = void>(
  options: CreateQueryOptions<QueryResult, ApplicationError>,
) => createQuery<QueryResult, ApplicationError>(options);

export const withOnSuccess = <Output>(
  onSuccess: ((data: Output) => MaybePromise<void>) | undefined,
  action: (data: Output) => MaybePromise<void>,
): ((data: Output) => MaybePromise<void>) => {
  return async (data) => {
    await onSuccess?.(data);
    await action(data);
  };
};
