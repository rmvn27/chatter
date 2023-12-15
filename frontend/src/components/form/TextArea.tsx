import {
  Field,
  type FieldPath,
  type FieldValues,
  type FormState,
} from "@modular-forms/solid";
import { createEffect, createSignal, onCleanup, onMount } from "solid-js";
import { FormElementLayout } from "./FormLayout.jsx";

type Props<Values extends FieldValues, FieldName extends FieldPath<Values>> = {
  label?: string;
  type?: string;
  placeholder?: string;
  initialValue?: string;
  form: FormState<Values>;
  name: FieldName;
  class?: string;
};

export function TextArea<
  Values extends FieldValues,
  FieldName extends FieldPath<Values>,
>(props: Props<Values, FieldName>) {
  const shape = "p-2 border-2 rounded-lg w-full";
  const color = "bg-inherit border-zinc-700 focus:border-zinc-50/50 focus:outline-none";

  const [rows, setRows] = createSignal(1);

  let area: HTMLTextAreaElement | undefined = undefined;

  // dynamically set the size of the rows
  // src: https://stackoverflow.com/a/70060676
  onMount(() => {
    if (area === undefined) return;

    // calculate the amount of rows needed
    // const inputListener = (e: Event) => {
    //   const target = e.target as HTMLTextAreaElement;

    //   const newLines = target.value.match(/\n/g)?.length ?? 1;
    //   console.log(target.value);
    //   setRows(newLines);
    // };
    // area.addEventListener("input", inputListener);

    // allow for using shift + enter for new lines
    // and enter to just submit the message
    //
    // src: https://stackoverflow.com/a/49389811
    const keypressListener = (e: KeyboardEvent) => {
      if (e.key === "Enter" && !e.shiftKey) {
        e.preventDefault();

        const submitEvent = new Event("submit", { cancelable: false });
        props.form.element?.dispatchEvent(submitEvent);
        // reset rows after submission
        setRows(1);
      }
    };
    area.addEventListener("keydown", keypressListener);

    onCleanup(() => {
      // area?.removeEventListener("input", inputListener);
      area?.removeEventListener("keydown", keypressListener);
    });
  });

  createEffect(() => {
    if (area === undefined) return;

    area.rows = rows();
  });

  return (
    <Field of={props.form} name={props.name}>
      {(field) => {
        const error = () => field.error;

        return (
          <FormElementLayout label={props.label} error={error} class={props.class}>
            <textarea
              {...field.props}
              id={field.name}
              class={`${shape} ${color} text-md text-zinc-400 h-full overflow-hidden resize-none`}
              placeholder={props.placeholder}
              rows={rows()}
              value={field.value as string}
              ref={(ref) => (area = ref)}
            />
          </FormElementLayout>
        );
      }}
    </Field>
  );
}
