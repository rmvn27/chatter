import {
  Field,
  type FieldPath,
  type FieldValues,
  type FormState,
} from "@modular-forms/solid";
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

  return (
    <Field of={props.form} name={props.name}>
      {(field) => {
        const error = () => field.error;

        return (
          <FormElementLayout label={props.label} error={error} class={props.class}>
            <textarea
              {...field.props}
              id={field.name}
              class={`${shape} ${color} text-md text-zinc-400 h-full`}
              placeholder={props.placeholder}
              value={field.value as string}
            />
          </FormElementLayout>
        );
      }}
    </Field>
  );
}
