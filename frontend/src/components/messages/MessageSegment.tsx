import { Message, PrettifiedMessage } from "@/models/messages";
import { MessagesState } from "@/signals/messagesState";
import { Accessor, Component, For, createMemo } from "solid-js";
import { MarkdownContent } from "./markdown/MarkdownContent";

type Props = {
  state: MessagesState;
  messages: Accessor<Message[]>;
};

export const MessageSegment: Component<Props> = (props) => {
  const displayMessages = createMemo(() =>
    props.state.createPrettifiedMessages(props.messages),
  );

  return <For each={displayMessages()}>{(m) => <Entry message={m} />}</For>;
};

type MessageProps = {
  message: PrettifiedMessage;
};

const Entry: Component<MessageProps> = (props) => {
  const message = () => props.message;

  return (
    <div class="py-2 px-3 flex flex-col gap-0.5 border-1 rounded-lg border-zinc-700 bg-zinc-700/25">
      <div class="flex flex-row items-center gap-2">
        <span class="text-md font-medium text-sky-300/80">{message().username}</span>

        <span class="text-md font-medium text-zinc-300/50">{message().date}</span>
      </div>

      <MarkdownContent content={message().content} />
    </div>
  );
};
