import type { Component, JSXElement } from "solid-js";
import { BaseLayout } from "./BaseLayout";

type Props = {
  size?: "md" | "lg" | "xl" | "2xl";
  children: JSXElement;
};

const sizeClasses = {
  md: "max-w-screen-md",
  lg: "max-w-screen-lg",
  xl: "max-w-screen-xl",
  "2xl": "max-w-screen-2xl",
};

export const ScreenLayout: Component<Props> = (props) => {
  const sizeClassesFn = () => sizeClasses[props.size ?? "lg"];

  return (
    <BaseLayout>
      <div class={`${sizeClassesFn()} w-full h-full mx-auto`}>{props.children}</div>
    </BaseLayout>
  );
};
