import { IconButton } from "@/components/lib/Button";
import { Participant, Team } from "@/models/teams";
import { TeamState } from "@/signals/teamState";
import { Component, For, Show } from "solid-js";
import { ParticipantsSidebarHeading } from "./ParticipantsSidebarHeading";

type Props = {
  state: TeamState;
};

export const ParticipantsSidebar: Component<Props> = (props) => {
  const layoutClasses = "h-full py-3 px-3 flex flex-col w-xs gap-2";
  const colorClasses = "border-l-2 border-zinc-700";

  return (
    <div class={`${layoutClasses} ${colorClasses}`}>
      <div class="w-full py-1">
        <ParticipantsSidebarHeading />
      </div>

      <div class="flex flex-col gap-1.5">
        <For each={props.state.participants}>
          {(p) => (
            <Entry
              team={props.state.team}
              participant={p}
              onRemove={props.state.removeParticipant}
            />
          )}
        </For>
      </div>
    </div>
  );
};

type EntryProps = {
  team: Team | undefined;
  participant: Participant;
  onRemove: (participant: Participant) => void;
};

export const Entry: Component<EntryProps> = (props) => {
  const participantColor = () =>
    props.participant.teamOwner ? "text-sky-300/90" : "text-zinc-300/90";

  const canDelete = () => {
    if (props.team === undefined || !props.team.isOwner) return false;

    return !props.participant.teamOwner;
  };

  return (
    <div class="px-2 py-1 rounded-lg flex flex-row items-center justify-between border border-zinc-700">
      <span
        class={`text-md font-medium ${participantColor()} overflow-hidden text-ellipsis`}
      >
        {props.participant.name}
      </span>

      <Show when={canDelete()}>
        <IconButton
          icon="i-lucide-x"
          size="xs"
          onClick={() => props.onRemove(props.participant)}
        />
      </Show>
    </div>
  );
};
