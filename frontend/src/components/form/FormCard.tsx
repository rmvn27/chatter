import { Form, type FieldValues, type FormState } from "@modular-forms/solid";
import type { JSX } from "solid-js";
import { Dynamic } from "solid-js/web";

export type Props<Values extends FieldValues> = {
  size?: "sm" | "md";
  label: string;
  actions?: JSX.Element;
  children?: JSX.Element;
  form: FormState<Values>;
  onSubmit: (values: Values) => void;
  headingTag?: keyof JSX.IntrinsicElements;
};

export const FormCard = <Values extends FieldValues>(props: Props<Values>) => {
  const size = () => props.size ?? "md";

  const gap = () => (size() === "md" ? "gap-4" : "gap-4");
  const headingSize = () => (size() === "md" ? "text-3xl" : "text-2xl");
  const sizes = "flex flex-col p-6 border-2";

  const headingTag = () => props.headingTag ?? "h1";

  return (
    <Form
      of={props.form}
      onSubmit={props.onSubmit}
      class={`${sizes} ${gap()} bg-zinc-800 rounded-2xl border-2 border-zinc-700`}
    >
      <Dynamic
        component={headingTag()}
        class={`${headingSize()} font-semibold text-zinc-300 select-none`}
      >
        {props.label}
      </Dynamic>

      <div class="flex flex-col gap-2">{props.children}</div>

      <div class="flex flex-row gap-2 justify-end w-full">{props.actions}</div>
    </Form>
  );
};
