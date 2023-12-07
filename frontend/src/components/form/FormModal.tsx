import type { ToggleSignal } from "@/lib/signals/toggle.js";
import type { FieldValues, FormState } from "@modular-forms/solid";
import type { JSX, JSXElement } from "solid-js";
import { Modal } from "../lib/Modal.jsx";
import { FormCard } from "./FormCard.jsx";

type Props<Values extends FieldValues> = {
  size?: "sm" | "md";
  label: string;
  form: FormState<Values>;
  opened: ToggleSignal;
  onSubmit: (values: Values) => void;
  children: JSX.Element;
  actions?: JSX.Element;
};

export const FormModal = <Values extends FieldValues>(
  props: Props<Values>,
): JSXElement => {
  const size = () => props.size ?? "md";

  return (
    <Modal opened={props.opened}>
      <FormCard
        size={size()}
        label={props.label}
        form={props.form}
        onSubmit={props.onSubmit}
        actions={props.actions}
        headingTag="h2"
      >
        {props.children}
      </FormCard>
    </Modal>
  );
};
