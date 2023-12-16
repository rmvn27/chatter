import type { ParentComponent } from "solid-js";

export const AuthLayout: ParentComponent = (props) => {
  return (
    <div class="h-full w-full flex flex-col items-center justify-center">
      <div class="max-w-screen-sm w-full flex flex-col gap-6">
        <div class="flex flex-row justify-center">
          <h1 class="text-5xl font-bold italic text-zinc-300">CHATTER</h1>
        </div>

        {props.children}
      </div>
    </div>
  );
};
