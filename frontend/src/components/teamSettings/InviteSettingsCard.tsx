import { InviteSettingsState } from "@/signals/teamSettings/inviteSettingsState";
import { Component, For } from "solid-js";
import { BaseCard } from "../layout/Card";
import { TextButton } from "../lib/Button";

type Props = {
  state: InviteSettingsState;
};

export const InviteSettingsCard: Component<Props> = (props) => {
  return (
    <BaseCard>
      <div class="h-full flex flex-row justify-between items-center">
        <h3 class="text-2xl font-semibold text-zinc-300 select-none">Invites</h3>

        <TextButton size="xs" style="pos" onClick={props.state.create}>
          CREATE
        </TextButton>
      </div>

      <div class="flex flex-col gap-1">
        <For each={props.state.invites}>
          {(i) => <InviteEntry invite={i} onDelete={() => props.state.delete(i)} />}
        </For>
      </div>
    </BaseCard>
  );
};

type EntryProps = {
  invite: string;
  onDelete: () => void;
};

export const InviteEntry: Component<EntryProps> = (props) => {
  const layoutProps = "py-2 flex flex-row justify-between items-center";

  return (
    <div class={`${layoutProps} border-b-1 border-zinc-300/10`}>
      <span class="text-md font-regular text-zinc-300/75">{props.invite}</span>

      <TextButton size="xs" style="neg" onClick={props.onDelete}>
        DELETE
      </TextButton>
    </div>
  );
};
