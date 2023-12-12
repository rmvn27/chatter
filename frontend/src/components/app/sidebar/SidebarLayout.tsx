import { Component, JSXElement } from "solid-js";

type Props = {
  teams: JSXElement;
  controls: JSXElement;
};

export const SidebarLayout: Component<Props> = (props) => {
  const layoutClasses = "h-full py-3 px-2 flex flex-col";
  const colorClasses = "border-r-2 border-zinc-300/10";

  return (
    <div class={`${layoutClasses} ${colorClasses}`}>
      <div class="flex-1 pb-1 flex flex-col gap-2">{props.teams}</div>

      <div class="pt-1 flex flex-col gap-2 border-t-1 border-zinc-300/10">
        {props.controls}
      </div>
    </div>
  );
};
