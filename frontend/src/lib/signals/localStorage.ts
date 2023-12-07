import type { JsonValue } from "@/lib/types";
import { createSignal } from "solid-js";

// Presist json serialized value in the local storage
// if undefined is set, remove it from the storage
export const createLocalStorageSignal = <T>(
  key: string,
  defaultValue: JsonValue | undefined = undefined,
) => {
  const maybeItem = localStorage.getItem(key);
  const defaultItem: T | undefined =
    maybeItem !== null ? JSON.parse(maybeItem) : defaultValue;
  const [value, setValue] = createSignal(defaultItem);

  const setNewValue = (newValue: T | undefined) => {
    if (newValue !== undefined) {
      localStorage.setItem(key, JSON.stringify(newValue));
    } else {
      localStorage.removeItem(key);
    }
    setValue(() => newValue);
  };

  return [value, setNewValue] as const;
};
