import { Component, JSXElement } from "solid-js";

type Props = {
  children: JSXElement;
};

export const TeamSettingsLayout: Component<Props> = (props) => {
  return (
    <div class="h-full w-full overflow-y-scroll	hide-scrollbar">
      <div class="pt-8 max-w-screen-lg mx-auto">
        <div class="flex flex-col gap-8">
          <h1 class="text-zinc-300 text-5xl font-semibold">Settings</h1>

          {props.children}
        </div>
      </div>
    </div>
  );
};
