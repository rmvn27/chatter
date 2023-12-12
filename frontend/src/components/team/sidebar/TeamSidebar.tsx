import { TeamState } from "@/signals/team/teamState";
import { Component } from "solid-js";
import { TeamSidebarControls } from "./TeamSidebarControls";
import { TeamSidebarHeading } from "./TeamSidebarHeading";
import { TeamSidebarLayout } from "./TeamSidebarLayout";

type Props = {
  state: TeamState;
};

export const TeamOverviewSidebar: Component<Props> = (props) => {
  const heading = <TeamSidebarHeading team={props.state.team} />;
  const channels = <></>;
  const controls = (
    <TeamSidebarControls
      team={props.state.team}
      onLeave={props.state.leave}
      onSettings={props.state.navToSettings}
    />
  );

  return (
    <TeamSidebarLayout heading={heading} channels={channels} controls={controls} />
  );
};
