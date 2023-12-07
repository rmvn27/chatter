import { createSignal, type Accessor, type Setter } from "solid-js";

export type ToggleSignal = {
  value: Accessor<boolean>;
  toggle: () => void;
  set: Setter<boolean>;
};

export const createToggle = (initialValue = false): ToggleSignal => {
  const [value, setValue] = createSignal(initialValue);

  return {
    value,
    toggle: () => {
      setValue((prev) => !prev);
    },
    set: setValue,
  };
};
