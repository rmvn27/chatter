import { Show, type Accessor, type Component, type JSX } from "solid-js";

type Props = {
  label?: string;
  error: Accessor<string>;
  children: JSX.Element;
  class?: string;
};

export const FormElementLayout: Component<Props> = (props) => {
  return (
    <div class={`flex flex-col gap-1 ${props.class ?? ""}`}>
      <Show when={props.label !== undefined}>
        <FormLabel value={props.label as string} />
      </Show>

      {props.children}

      <FormErrorLabel value={props.error()} />
    </div>
  );
};

type LabelProps = {
  value: string;
};
const FormLabel: Component<LabelProps> = (props) => (
  <label class="select-none">
    <span class="text-zinc-400 text-sm font-medium">{props.value}</span>
  </label>
);

type ErrorProps = {
  value: string | undefined;
};
export const FormErrorLabel: Component<ErrorProps> = (props) => {
  return (
    <Show when={props.value}>
      <label class="select-none">
        <span class="text-sm font-medium text-red-400">{props.value}</span>
      </label>
    </Show>
  );
};
