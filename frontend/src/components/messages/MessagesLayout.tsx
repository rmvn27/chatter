import { Component, JSXElement } from "solid-js";

type Props = {
  messages: JSXElement;
  chatInput: JSXElement;
};

export const MessagesLayout: Component<Props> = (props) => {
  return (
    <div class="w-full h-full flex flex-col gap-2 p-4">
      {props.messages}
      {props.chatInput}
    </div>
  );
};
