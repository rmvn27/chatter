import { useIntersecionObservable } from "@/lib/signals/intersectionObservable";
import { MessagesState } from "@/signals/messagesState";
import { Component, For, Show, createSignal } from "solid-js";
import { MessageSegment } from "./MessageSegment";

type Props = {
  state: MessagesState;
};

export const ChatMessages: Component<Props> = (props) => {
  const historySegments = () => props.state.historySegments;

  const [intersectionElement, setIntersectionElement] = createSignal<
    Element | undefined
  >(undefined);

  useIntersecionObservable(intersectionElement, (isIntersecting) => {
    if (isIntersecting) {
      props.state.fetchNextHistoryPage();
    }
  });

  const liveMessages = () => props.state.liveMessages();
  return (
    <div class="h-full overflow-y-scroll hide-scrollbar flex flex-col-reverse gap-2">
      <Show when={liveMessages().length !== 0}>
        <div class="flex flex-col-reverse gap-2">
          <MessageSegment state={props.state} messages={props.state.liveMessages} />
        </div>
      </Show>

      <For each={historySegments()}>
        {(segment) => (
          <div class="flex flex-col-reverse gap-2">
            <MessageSegment state={props.state} messages={() => segment} />
          </div>
        )}
      </For>

      <div class="h-10" ref={setIntersectionElement}></div>
    </div>
  );
};
