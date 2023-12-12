import { TextField } from "@/components/form/TextField";
import { BaseCard } from "@/components/layout/Card";
import { TextButton } from "@/components/lib/Button";
import { Modal } from "@/components/lib/Modal";
import { createZodForm } from "@/lib/signals/form";
import { ToggleSignal } from "@/lib/signals/toggle";
import {
  CreateTeamForm,
  JoinTeamForm,
  createTeamForm,
  joinTeamForm,
} from "@/models/teams";
import { AddTeamState } from "@/signals/app/addTeamState";
import { Form } from "@modular-forms/solid";
import { Component } from "solid-js";

type Props = {
  opened: ToggleSignal;
};

// allow the user to either create a new team or join a exinsting one
export const AddTeamModal: Component<Props> = (props) => {
  const state = AddTeamState.create(props.opened);

  return (
    <Modal opened={props.opened} onClose={() => props.opened.set(false)}>
      <BaseCard class="flex flex-col w-xl bg-zinc-800">
        <NewTeamForm onNewTeam={state().createNewTeam} />

        <div class="h-0.5 rounded-lg bg-zinc-700" />

        <JoinExistingTeamForm onJoin={state().joinTeam} />
      </BaseCard>
    </Modal>
  );
};

type NewTeamProps = {
  onNewTeam: (form: CreateTeamForm) => void;
};

export const NewTeamForm: Component<NewTeamProps> = (props) => {
  const form = createZodForm(createTeamForm);

  return (
    <Form of={form} onSubmit={props.onNewTeam} class="flex flex-col gap-4">
      <h2 class="text-xl font-semibold text-zinc-300 select-none">Create New Team</h2>

      <TextField form={form} name="name" placeholder="Team Name" />

      <div class="flex flex-row justify-end">
        <TextButton size="xs" style="pos">
          CREATE
        </TextButton>
      </div>
    </Form>
  );
};

type JoinTeamProps = {
  onJoin: (form: JoinTeamForm) => void;
};

export const JoinExistingTeamForm: Component<JoinTeamProps> = (props) => {
  const form = createZodForm(joinTeamForm);

  return (
    <Form of={form} onSubmit={props.onJoin} class="flex flex-col gap-4">
      <h2 class="text-xl font-semibold text-zinc-300 select-none">
        Join Existing Team
      </h2>

      <TextField form={form} name="invite" placeholder="Invite" />

      <div class="flex flex-row justify-end">
        <TextButton size="xs" style="highlight">
          JOIN
        </TextButton>
      </div>
    </Form>
  );
};
