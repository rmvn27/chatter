import { Outlet } from "@solidjs/router";
import { Component, JSXElement } from "solid-js";

type Props = {
  sidebar: JSXElement;
  participants: JSXElement;
};

export const TeamLayout: Component<Props> = (props) => {
  return (
    <div class="flex flex-row h-full w-full">
      {props.sidebar}
      <div class="flex-1 w-full">
        <Outlet />
      </div>
      {props.participants}
    </div>
  );
};
