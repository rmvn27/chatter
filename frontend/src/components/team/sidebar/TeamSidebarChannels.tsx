import { TeamState } from "@/signals/team/teamState";
import { Component, For } from "solid-js";
import { TeamSidebarChannelsEntry } from "./TeamSidebarChannelsEntry";

type Props = {
  state: TeamState;
};

export const TeamSidebarChannels: Component<Props> = (props) => {
  return (
    <div class="flex flex-col gap-1.5">
      <For each={props.state.channels}>
        {(c) => (
          <TeamSidebarChannelsEntry
            channel={c}
            selected={props.state.channelSlug() == c.slug}
            onClick={() => props.state.navToMessages(c.slug)}
          />
        )}
      </For>
    </div>
  );
};
