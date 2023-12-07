import { A } from "@solidjs/router";
import type { Component } from "solid-js";

type TextButtonProps = {
  children: string;
  size?: "lg" | "md" | "sm" | "xs";
  style?: "default" | "highlight" | "neg" | "pos";
  onClick?: () => void;
  type?: "submit" | "reset" | "button";
};

const textButtonColorMap = {
  default: "hover:bg-zinc-300/10 text-zinc-300",
  highlight: "hover:bg-sky-300/10 border-1 border-sky-400 text-zinc-400",
  neg: "hover:bg-red-300/10 border-1 border-red-400 text-zinc-400",
  pos: "hover:bg-green-300/10 border-1 border-green-400 text-zinc-400",
};

const textButtonSizeMap = {
  lg: {
    shape: "py-2 px-3 rounded-lg",
    text: "text-2xl font-semibold",
  },
  md: {
    shape: "py-2 px-3 rounded-lg",
    text: "text-xl font-semibold",
  },
  sm: {
    shape: "py-1 px-2 rounded-lg",
    text: "text-md font-semibold",
  },
  xs: {
    shape: "py-0.5 px-2 rounded-lg",
    text: "text-sm font-medium",
  },
};

export const TextButton: Component<TextButtonProps> = (props) => {
  const { shape, text } = textButtonSizeMap[props.size ?? "md"];
  const color = textButtonColorMap[props.style ?? "default"];

  return (
    <button
      onClick={(e) => {
        e.stopPropagation();
        props.onClick?.();
      }}
      type={props.type}
      class={`${shape} ${text} ${color} transition`}
    >
      {props.children}
    </button>
  );
};

type IconButtonProps = {
  icon: string;
  onClick?: () => void;
  size?: "sm" | "md";
};

const iconButtonSizeMap = {
  sm: {
    button: "p-1 w-8.5 h-8.5",
    icon: "w-6.25 h-6.25",
  },
  md: {
    button: "p-3 w-12 h-12",
    icon: "w-6 h-6",
  },
};

export const IconButton = (props: IconButtonProps) => {
  const { button, icon } = iconButtonClasses(
    props.size ?? "md",
    props.onClick !== undefined,
  );

  return (
    <button
      onClick={(e) => {
        e.stopPropagation();
        props.onClick?.();
      }}
      class={button}
    >
      <div class={`${icon} ${props.icon}`} />
    </button>
  );
};

type NavIconButtonProps = {
  icon: string;
  route: string;
  size?: "sm" | "md";
};

export const NavIconButton = (props: NavIconButtonProps) => {
  const { button, icon } = iconButtonClasses(props.size ?? "md", true);

  return (
    <A href={props.route} class={button}>
      <div class={`${props.icon} ${icon}`} />
    </A>
  );
};

export const iconButtonClasses = (size: "sm" | "md", hasClick: boolean) => {
  const { icon: iconBase, button: buttonBase } = iconButtonSizeMap[size];
  const hover = hasClick ? "hover:bg-zinc-50/15" : "";

  const button = `${buttonBase} ${hover} rounded-lg justify-center items-center transition`;
  const icon = `${iconBase} text-zinc-300`;

  return {
    button,
    icon,
  };
};
