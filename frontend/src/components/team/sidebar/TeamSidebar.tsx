import { TeamState } from "@/signals/teamState";
import { Component } from "solid-js";
import { TeamSidebarChannels } from "./TeamSidebarChannels";
import { TeamSidebarControls } from "./TeamSidebarControls";
import { TeamSidebarHeading } from "./TeamSidebarHeading";
import { TeamSidebarLayout } from "./TeamSidebarLayout";

type Props = {
  state: TeamState;
};

export const TeamOverviewSidebar: Component<Props> = (props) => {
  const heading = <TeamSidebarHeading team={props.state.team} />;
  const channels = <TeamSidebarChannels state={props.state} />;
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
