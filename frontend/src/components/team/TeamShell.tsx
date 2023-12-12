import { TeamState } from "@/signals/team/teamState";
import { Component } from "solid-js";
import { TeamLayout } from "./TeamLayout";
import { ParticipantsSidebar } from "./participants/ParticipantsSidebar";
import { TeamOverviewSidebar } from "./sidebar/TeamSidebar";

type Props = {
  state: TeamState;
};

export const TeamShell: Component<Props> = (props) => {
  const sidebar = <TeamOverviewSidebar state={props.state} />;
  const participants = <ParticipantsSidebar state={props.state} />;

  return <TeamLayout sidebar={sidebar} participants={participants} />;
};
