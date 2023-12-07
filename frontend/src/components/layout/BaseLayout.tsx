import type { Component, JSX } from "solid-js";

export type Props = {
  children?: JSX.Element;
  navbar: JSX.Element;
};

export const BaseLayout: Component<Props> = (props) => {
  return (
    <div class="flex flex-col h-full">
      {props.navbar}

      <div class="py-8 px-8 w-full h-full">{props.children}</div>
    </div>
  );
};
