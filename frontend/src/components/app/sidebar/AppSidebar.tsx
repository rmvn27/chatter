import { IconButton } from "@/components/lib/Button";
import { AppShellState } from "@/signals/app/appShellState";
import { Component, For } from "solid-js";
import { SidebarLayout } from "./SidebarLayout";
import { TeamSidebarIcon } from "./TeamSidebarIcon";

type Props = {
  shell: AppShellState;
};

export const AppSidebar: Component<Props> = (props) => {
  const teams = (
    <For each={props.shell.teams}>
      {(t) => <TeamSidebarIcon team={t} selected={t.slug == props.shell.teamSlug()} />}
    </For>
  );
  const controls = (
    <>
      <IconButton
        size="md"
        icon="i-lucide-plus"
        onClick={props.shell.addTeamModalToggle.toggle}
      />
      <IconButton
        size="md"
        icon="i-lucide-settings"
        onClick={props.shell.navToSettings}
      />
      <IconButton size="md" icon="i-lucide-log-out" onClick={props.shell.logout} />
    </>
  );

  return <SidebarLayout teams={teams} controls={controls} />;
};
