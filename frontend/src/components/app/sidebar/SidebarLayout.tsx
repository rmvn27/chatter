import { Sidebar } from "@/components/lib/Sidebar";
import { Component, JSXElement } from "solid-js";

type Props = {
  teams: JSXElement;
  controls: JSXElement;
};

export const SidebarLayout: Component<Props> = (props) => {
  return (
    <Sidebar side="right" class="px-2">
      <div class="flex-1 pb-1 flex flex-col gap-2">{props.teams}</div>

      <div class="pt-1 flex flex-col gap-2 border-t-1 border-zinc-700">
        {props.controls}
      </div>
    </Sidebar>
  );
};
