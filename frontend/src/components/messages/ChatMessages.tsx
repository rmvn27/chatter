import { DisplayTeamMessage, TeamMessage } from "@/models/messages";
import { MessagesState } from "@/signals/messagesState";
import { Accessor, Component, For, createMemo, onCleanup, onMount } from "solid-js";

type Props = {
  state: MessagesState;
};

export const ChatMessages: Component<Props> = (props) => {
  const historySegments = () => props.state.messagesQuery.data?.pages ?? [];

  let intersectionObserveable: HTMLElement | undefined = undefined;

  onMount(() => {
    if (intersectionObserveable == undefined) return;

    const observer = new IntersectionObserver((entries) => {
      if (entries[0]?.isIntersecting) {
        const query = props.state.messagesQuery;

        if (query.hasNextPage && !query.isFetchingNextPage) {
          query.fetchNextPage();
        }
      }
    });

    observer.observe(intersectionObserveable);

    onCleanup(() => observer.disconnect());
  });

  return (
    <div class="flex-1 h-full overflow-y-scroll hide-scrollbar flex flex-col-reverse gap-2">
      <div class="flex flex-col-reverse gap-2">
        <MessageSegment state={props.state} messages={props.state.liveMessages} />
      </div>

      <For each={historySegments()}>
        {(segment) => (
          <div class="flex flex-col-reverse gap-2">
            <MessageSegment state={props.state} messages={() => segment} />
          </div>
        )}
      </For>

      <div class="h-10" ref={(el) => (intersectionObserveable = el)}></div>
    </div>
  );
};

type MessageSegmentProps = {
  state: MessagesState;
  messages: Accessor<TeamMessage[]>;
};

const MessageSegment: Component<MessageSegmentProps> = (props) => {
  const displayMessages = createMemo(() => {
    return props.state.createDisplayMessages(props.messages);
  });

  return <For each={displayMessages()()}>{(m) => <Message message={m} />}</For>;
};

type MessageProps = {
  message: DisplayTeamMessage;
};

const Message: Component<MessageProps> = (props) => {
  const message = () => props.message;

  return (
    <div class="py-2 px-3 flex-flex-col gap-2 border-1 rounded-lg border-zinc-300/10 bg-zinc-700/25">
      <div class="flex flex-row items-center gap-2">
        <span class="text-md font-medium text-sky-300/80">{message().username}</span>

        <span class="text-md font-medium text-zinc-300/50">{message().date}</span>
      </div>
      <p class="text-lg font-regular text-zinc-300">{message().content}</p>
    </div>
  );
};
