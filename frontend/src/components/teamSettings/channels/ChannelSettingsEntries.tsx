import { TextButton } from "@/components/lib/Button";
import { Channel } from "@/models/channels";
import { ChannelSettingsState } from "@/signals/teamSettings/channelSettingsState";
import { Component, For } from "solid-js";

type Props = {
  state: ChannelSettingsState;
};

export const ChannelSettingsEntries: Component<Props> = (props) => {
  return (
    <div class="flex flex-col gap-1">
      <For each={props.state.channels}>
        {(c) => (
          <ChannelEntry
            channel={c}
            onDelete={() => props.state.deleteChannel(c.slug)}
          />
        )}
      </For>
    </div>
  );
};

type EntryProps = {
  channel: Channel;
  onDelete: () => void;
};

const ChannelEntry: Component<EntryProps> = (props) => {
  const layoutProps = "py-2 flex flex-row justify-between items-center";

  return (
    <div class={`${layoutProps} border-b-1 border-zinc-700`}>
      <span class="text-md font-regular text-zinc-300/75">{props.channel.name}</span>

      <TextButton size="xs" style="neg" onClick={props.onDelete}>
        Delete
      </TextButton>
    </div>
  );
};
