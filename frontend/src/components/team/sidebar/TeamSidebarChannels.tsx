import { Channel } from "@/models/channels";
import { TeamState } from "@/signals/teamState";
import { Component, For } from "solid-js";

type Props = {
  state: TeamState;
};

export const TeamSidebarChannels: Component<Props> = (props) => {
  return (
    <div class="flex flex-col gap-1.5">
      <For each={props.state.channels}>
        {(c) => (
          <Entry
            channel={c}
            selected={props.state.channelSlug() == c.slug}
            onClick={() => props.state.navToMessages(c.slug)}
          />
        )}
      </For>
    </div>
  );
};

type EntryProps = {
  channel: Channel;
  selected: boolean;
  onClick: () => void;
};

export const Entry: Component<EntryProps> = (props) => {
  const colorButtonProps = () => {
    const base = "transition border";

    if (props.selected) {
      return `${base} border-sky-400/10 bg-sky-400/50`;
    } else {
      return `${base} border-zinc-300/15 hover:bg-zinc-50/15`;
    }
  };

  const layoutProps = "px-2 py-1 rounded-lg flex flex-row items-center justify-between";

  return (
    <button class={`${layoutProps} ${colorButtonProps()}`} onClick={props.onClick}>
      <span
        class={`text-md font-medium text-zinc-300/90 overflow-hidden text-ellipsis`}
      >
        {props.channel.name}
      </span>
    </button>
  );
};
