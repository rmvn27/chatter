import type { UserSettingsState } from "@/signals/userSettingsState";
import type { Component } from "solid-js";
import { FormCard } from "../form/FormCard";
import { FormErrorLabel } from "../form/FormLayout";
import { TextField } from "../form/TextField";
import { TextButton } from "../lib/Button";

type Props = {
  state: UserSettingsState;
};

export const UpdatePasswordCard: Component<Props> = (props) => {
  const form = () => props.state.updatePasswordForm;

  const actions = (
    <TextButton style="highlight" size="sm" type="submit">
      UPDATE
    </TextButton>
  );
  return (
    <FormCard
      label="Update Password"
      headingTag="h3"
      size="sm"
      form={form()}
      onSubmit={props.state.updatePassword}
      actions={actions}
    >
      <TextField
        form={form()}
        name="currentPassword"
        label="Current Password"
        type="password"
      />
      <TextField
        form={form()}
        name="newPassword"
        label="New Password"
        type="password"
      />
      <TextField
        form={form()}
        name="newPasswordAgain"
        label="New Password (Again)"
        type="password"
      />

      <FormErrorLabel value={props.state.updateError} />
    </FormCard>
  );
};
