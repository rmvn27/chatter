import { UserSettingsState } from "@/signals/userSettingsState";
import type { Component } from "solid-js";
import { FormCard } from "../form/FormCard";
import { TextField } from "../form/TextField";
import { TextButton } from "../lib/Button";

type Props = {
  state: UserSettingsState;
};

export const GeneralUserSettingsCard: Component<Props> = (props) => {
  const form = () => props.state.generalSettingsForm;

  const actions = (
    <TextButton style="highlight" size="sm" type="submit">
      UPDATE
    </TextButton>
  );
  return (
    <FormCard
      label="General"
      headingTag="h3"
      size="sm"
      form={form()}
      onSubmit={props.state.updateGeneralUserData}
      actions={actions}
    >
      <TextField form={form()} name="name" placeholder="Display Name" />
    </FormCard>
  );
};
