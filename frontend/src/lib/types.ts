export type JsonValue =
  | string
  | number
  | null
  | JsonValue[]
  | { [key: string]: JsonValue };

export type MaybePromise<T> = Promise<T> | T;
