import { TeamChannel } from "@/models/channels";
import { Component } from "solid-js";

type Props = {
  channel: TeamChannel;
  selected: boolean;
  onClick: () => void;
};

export const TeamSidebarChannelsEntry: Component<Props> = (props) => {
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
      <span class={`text-md font-medium text-zinc-300/90`}>{props.channel.name}</span>
    </button>
  );
};
