import { Team } from "@/models/teams";
import { Component, Show } from "solid-js";

type Props = {
  team: Team | undefined;
};

export const TeamSidebarHeading: Component<Props> = (props) => {
  const textClasses = "text-xl font-medium text-zinc-300 select-none";
  const borderClasses = "pb-1 border-b border-zinc-300/10";

  return (
    <Show when={props.team !== undefined}>
      <h2 class={`${textClasses} ${borderClasses}`}>{props.team?.name}</h2>
    </Show>
  );
};
