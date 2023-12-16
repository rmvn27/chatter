import { Card } from "@/components/lib/Card";
import { ChannelSettingsState } from "@/signals/teamSettings/channelSettingsState";
import { Component } from "solid-js";
import { ChannelSettingsEntries } from "./ChannelSettingsEntries";
import { CreateChannelForm } from "./CreateChannelForm";

type Props = {
  state: ChannelSettingsState;
};

export const ChannelsSettingsCard: Component<Props> = (props) => {
  return (
    <Card label="Channels" labelTag="h3">
      <ChannelSettingsEntries state={props.state} />
      <div class="h-0.5 rounded-lg bg-zinc-700" />
      <CreateChannelForm state={props.state} />
    </Card>
  );
};
