import { Component, JSXElement } from "solid-js";

type Props = {
  heading: JSXElement;
  channels: JSXElement;
  controls: JSXElement;
};

export const TeamSidebarLayout: Component<Props> = (props) => {
  const layoutClasses = "h-full py-3 px-3 flex flex-col gap-2 w-xs";
  const colorClasses = "border-r-2 border-zinc-300/10";

  return (
    <div class={`${layoutClasses} ${colorClasses}`}>
      <div class="w-full py-1">{props.heading}</div>
      <div class="flex-1 w-full h-full">{props.channels}</div>
      <div class="w-full">{props.controls}</div>
    </div>
  );
};
