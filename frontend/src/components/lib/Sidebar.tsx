import { Component, JSXElement } from "solid-js";

type Props = {
  children: JSXElement;
  side: "left" | "right";
  class?: string;
};

export const Sidebar: Component<Props> = (props) => {
  const layoutClasses = "h-full py-3 flex flex-col";

  const border = () => (props.side === "left" ? "border-l-2" : "border-r-2");
  const colorClasses = () => `${border()} border-zinc-700`;
  const extraClasses = () => props.class ?? "";

  return (
    <div class={`${layoutClasses} ${colorClasses()} ${extraClasses()}`}>
      {props.children}
    </div>
  );
};
