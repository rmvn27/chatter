import { Team } from "@/models/teams";
import { Component, Show } from "solid-js";

type Props = {
  team: Team | undefined;
  onLeave: () => void;
  onSettings: () => void;
};

export const TeamSidebarControls: Component<Props> = (props) => {
  const controls = (team: Team | undefined) =>
    team?.isOwner ? (
      <ControlButton
        icon="i-lucide-settings"
        label="Settings"
        onClick={props.onSettings}
      />
    ) : (
      <ControlButton
        icon="i-lucide-log-out"
        label="Leave Team"
        onClick={props.onLeave}
      />
    );

  return (
    <Show when={props.team !== undefined}>
      <div class="pt-1 border-t-1 border-zinc-300/10">{controls(props.team)}</div>
    </Show>
  );
};

type ButtonProps = {
  icon: string;
  label: string;
  onClick: () => void;
};

const ControlButton: Component<ButtonProps> = (props) => {
  const layoutProps = "w-full h-12 px-3 py-2 items-center flex flex-row gap-2.5";

  return (
    <button
      class={`${layoutProps} rounded-lg transition hover:bg-zinc-50/15`}
      onClick={props.onClick}
    >
      <div class={`${props.icon} w-6 h-6 text-zinc-300`} />
      <span class="text-lg font-medium text-zinc-300">{props.label}</span>
    </button>
  );
};
