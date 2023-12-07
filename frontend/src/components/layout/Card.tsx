import type { Component, JSX, JSXElement } from "solid-js";
import { Dynamic } from "solid-js/web";

type Props = {
  label: string;
  labelTag?: keyof JSX.IntrinsicElements;
  children: JSXElement;
  class?: string;
};

export const Card: Component<Props> = (props) => {
  const labeltag = () => props.labelTag ?? "h2";
  const extraClasses = () => props.class ?? "";

  return (
    <BaseCard class={extraClasses()}>
      <Dynamic
        component={labeltag()}
        class={`text-2xl font-semibold text-zinc-300 select-none`}
      >
        {props.label}
      </Dynamic>

      {props.children}
    </BaseCard>
  );
};

type BaseCardProps = {
  children: JSXElement;
  class?: string;
};

export const BaseCard: Component<BaseCardProps> = (props) => {
  const extraClasses = () => props.class ?? "";

  return (
    <div
      class={`flex flex-col gap-4 p-6 rounded-2xl border-2 border-zinc-700 ${extraClasses()}`}
    >
      {props.children}
    </div>
  );
};
