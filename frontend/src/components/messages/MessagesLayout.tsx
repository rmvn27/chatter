import { Component, JSXElement } from "solid-js";

type Props = {
  messages: JSXElement;
  chatInput: JSXElement;
};

export const MessagesLayout: Component<Props> = (props) => {
  return (
    <div class="w-full h-full flex flex-col gap-4 p-4">
      {props.messages}
      <div>{props.chatInput}</div>
    </div>
  );
};
