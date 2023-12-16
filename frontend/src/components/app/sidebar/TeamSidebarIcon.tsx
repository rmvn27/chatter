import { navigationRoutes } from "@/config/routes";
import { Team } from "@/models/teams";
import { useNavigate } from "@solidjs/router";
import { Component, createMemo } from "solid-js";

type Props = {
  team: Team;
  selected: boolean;
};

export const TeamSidebarIcon: Component<Props> = (props) => {
  const nav = useNavigate();

  const iconText = createMemo(() => {
    let output = "";
    const [first, second] = props.team.name.split(" ");
    if (first !== undefined) output += first.at(0)?.toUpperCase() ?? "";
    if (second !== undefined) output += second.at(0)?.toUpperCase() ?? "";

    return output;
  });

  const baseButtonProps =
    "p-2 w-12 h-12 rounded-lg rounded-lg justify-center items-center";
  const colorButtonProps = () => {
    const base = "transition border";

    if (props.selected) {
      return `${base} border-sky-400/10 bg-sky-400/50`;
    } else {
      return `${base} border-zinc-300/15 hover:bg-zinc-50/15`;
    }
  };
  return (
    <button
      onClick={() => nav(navigationRoutes.team(props.team.slug).base)}
      class={`${baseButtonProps} ${colorButtonProps()}`}
    >
      <span class="text-lg font-semibold text-zinc-300 w-8 h-8">{iconText()}</span>
    </button>
  );
};
