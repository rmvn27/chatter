import { Sidebar } from "@/components/lib/Sidebar";
import { Component, JSXElement } from "solid-js";

type Props = {
  heading: JSXElement;
  channels: JSXElement;
  controls: JSXElement;
};

export const TeamSidebarLayout: Component<Props> = (props) => {
  return (
    <Sidebar side="right" class="px-3 gap-2 w-xs">
      <div class="w-full py-1">{props.heading}</div>
      <div class="flex-1 w-full h-full">{props.channels}</div>
      <div class="w-full">{props.controls}</div>
    </Sidebar>
  );
};
