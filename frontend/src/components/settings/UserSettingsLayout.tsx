import { Component, JSXElement } from "solid-js";

type Props = {
  children: JSXElement;
};

export const UserSettingsLayout: Component<Props> = (props) => {
  return (
    <div class="h-full w-full">
      <div class="pt-8 max-w-screen-md mx-auto">
        <div class="flex flex-col gap-8">
          <h1 class="text-zinc-300 text-5xl font-semibold">User - Settings</h1>

          {props.children}
        </div>
      </div>
    </div>
  );
};
