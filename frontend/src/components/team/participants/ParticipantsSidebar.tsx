import { TeamState } from "@/signals/team/teamState";
import { Component, For } from "solid-js";
import { ParticipantsSidebarHeading } from "./ParticipantsSidebarHeading";
import { TeamParticipantEntry } from "./TeamParticipant";

type Props = {
  state: TeamState;
};

export const ParticipantsSidebar: Component<Props> = (props) => {
  const layoutClasses = "h-full py-3 px-3 flex flex-col w-xs gap-3";
  const colorClasses = "border-l-2 border-zinc-300/10";

  return (
    <div class={`${layoutClasses} ${colorClasses}`}>
      <div class="w-full py-1">
        <ParticipantsSidebarHeading />
      </div>

      <div class="flex flex-col gap-1.5">
        <For each={props.state.participants}>
          {(p) => (
            <TeamParticipantEntry
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
