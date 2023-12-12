import { AppShellState } from "@/signals/app/appShellState";
import { Outlet } from "@solidjs/router";
import { Component } from "solid-js";
import { Sidebar } from "./sidebar/Sidebar";

type Props = {
  shellState: AppShellState;
};

export const AppShell: Component<Props> = (props) => {
  return (
    <div class="w-full h-full flex flex-row">
      <Sidebar shell={props.shellState} />

      <div class="w-full">
        <Outlet />
      </div>
    </div>
  );
};
