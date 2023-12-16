import {
  Field,
  type FieldPath,
  type FieldValues,
  type FormState,
} from "@modular-forms/solid";
import { createEffect, createSignal, onCleanup } from "solid-js";
import { FormElementLayout } from "./FormLayout.jsx";

type Props<Values extends FieldValues, FieldName extends FieldPath<Values>> = {
  label?: string;
  placeholder?: string;
  form: FormState<Values>;
  name: FieldName;
  onEnter: () => void;
};

// custom text area that that allow for growing ans shrinking depending by the amount of line in it
// and submits automatically when the enter key is pressed except when the shift key is also pressed
export function TextArea<
  Values extends FieldValues,
  FieldName extends FieldPath<Values>,
>(props: Props<Values, FieldName>) {
  const shape = "p-2 border-2 rounded-lg w-full";
  const color = "bg-inherit border-zinc-700 focus:border-zinc-50/50 focus:outline-none";

  const [rows, setRows] = createSignal(1);
  const [textArea, setArea] = createSignal<HTMLTextAreaElement | undefined>(undefined);

  // dynamically set the size of the rows
  // src: https://stackoverflow.com/a/70060676
  createEffect(() => {
    const area = textArea();
    if (area === undefined) return;

    // calculate the amount of rows needed
    const inputListener = (e: Event) => {
      const target = e.target as HTMLTextAreaElement;

      const newLines = target.value.split("\n")?.length ?? 1;
      setRows(newLines);
    };
    area.addEventListener("input", inputListener);

    // allow for using shift + enter for new lines
    // and enter to just submit the message
    //
    // src: https://stackoverflow.com/a/49389811
    const keypressListener = (e: KeyboardEvent) => {
      if (e.key === "Enter" && !e.shiftKey) {
        e.preventDefault();
        props.onEnter();
        setRows(1);
      }
    };
    area.addEventListener("keydown", keypressListener);

    onCleanup(() => {
      area?.removeEventListener("input", inputListener);
      area?.removeEventListener("keydown", keypressListener);
    });
  });

  return (
    <Field of={props.form} name={props.name}>
      {(field) => {
        const error = () => field.error;

        return (
          <FormElementLayout label={props.label} error={error}>
            <textarea
              {...field.props}
              id={field.name}
              class={`${shape} ${color} text-md text-zinc-400 h-full overflow-hidden resize-none`}
              placeholder={props.placeholder}
              value={field.value as string}
              rows={rows()}
              ref={setArea}
            />
          </FormElementLayout>
        );
      }}
    </Field>
  );
}
