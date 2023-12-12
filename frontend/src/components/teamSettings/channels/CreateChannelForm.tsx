import { ChannelSettingsState } from "@/signals/teamSettings/channelSettingsState";
import { Form } from "@modular-forms/solid";
import type { Component } from "solid-js";
import { TextField } from "../../form/TextField";
import { TextButton } from "../../lib/Button";

type Props = {
  state: ChannelSettingsState;
};

export const CreateChannelForm: Component<Props> = (props) => {
  const state = () => props.state;

  return (
    <div class="flex flex-col gap-2 w-full">
      <h3 class="text-lg font-medium text-zinc-300 select-none">New Channel</h3>

      <Form
        of={state().createChannelForm}
        onSubmit={state().createChannel}
        class="flex flex-row items-center gap-4"
      >
        <TextField
          form={state().createChannelForm}
          name={"name"}
          placeholder="Name"
          class="w-full"
        />
        <TextButton size="sm" type="submit" style="pos">
          Create
        </TextButton>
      </Form>
    </div>
  );
};
