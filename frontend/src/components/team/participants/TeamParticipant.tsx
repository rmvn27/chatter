import { IconButton } from "@/components/lib/Button";
import { Team, TeamParticipant } from "@/models/teams";
import { Component, Show } from "solid-js";

type Props = {
  team: Team | undefined;
  participant: TeamParticipant;
  onRemove: (participant: TeamParticipant) => void;
};

export const TeamParticipantEntry: Component<Props> = (props) => {
  const participantColor = () =>
    props.participant.teamOwner ? "text-sky-300/90" : "text-zinc-300/90";

  const canDelete = () => {
    if (props.team === undefined || !props.team.isOwner) return false;

    return !props.participant.teamOwner;
  };

  return (
    <div class="px-2 py-1 rounded-lg flex flex-row items-center justify-between border border-zinc-300/10">
      <span class={`text-md font-medium ${participantColor()}`}>
        {props.participant.username}
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
