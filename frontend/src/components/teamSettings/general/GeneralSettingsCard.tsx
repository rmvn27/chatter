import { GeneralTeamSettingsState } from "@/signals/teamSettings/generalSettingsState";
import { Form } from "@modular-forms/solid";
import type { Component } from "solid-js";
import { TextField } from "../../form/TextField";
import { Card } from "../../layout/Card";
import { TextButton } from "../../lib/Button";
import { DeleteTeamModal } from "./DeleteTeamModal";

type Props = {
  state: GeneralTeamSettingsState;
};

export const GeneralSettingsCard: Component<Props> = (props) => {
  const updateTeamForm = () => props.state.updateTeamForm;
  const deleteToggle = () => props.state.deleteToggle;

  return (
    <>
      <Card label="General" labelTag="h3">
        <Form
          of={updateTeamForm()}
          onSubmit={props.state.updateTeam}
          class="flex flex-col gap-4"
        >
          <TextField form={updateTeamForm()} name="name" label="Name" />

          <div class="flex flex-row justify-between">
            <TextButton
              style="neg"
              size="sm"
              onClick={deleteToggle().toggle}
              type="button"
            >
              DELETE
            </TextButton>

            <TextButton style="highlight" size="sm" type="submit">
              UPDATE
            </TextButton>
          </div>
        </Form>
      </Card>

      <DeleteTeamModal toggle={deleteToggle()} onDelete={props.state.deleteTeam} />
    </>
  );
};
