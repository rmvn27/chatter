import { GeneralTeamSettingsState } from "@/signals/teamSettings/generalSettingsState";
import { InviteSettingsState } from "@/signals/teamSettings/inviteSettingsState";
import { Component } from "solid-js";
import { GeneralSettingsCard } from "./GeneralSettingsCard";
import { InviteSettingsCard } from "./InviteSettingsCard";
import { TeamSettingsLayout } from "./TeamSettingsLayout";

type Props = {
  generalState: GeneralTeamSettingsState;
  inviteState: InviteSettingsState;
};

export const TeamSettings: Component<Props> = (props) => {
  return (
    <TeamSettingsLayout>
      <GeneralSettingsCard state={props.generalState} />
      <InviteSettingsCard state={props.inviteState} />
    </TeamSettingsLayout>
  );
};
