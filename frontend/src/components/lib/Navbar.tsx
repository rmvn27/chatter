import { A } from "@solidjs/router";
import type { Component, JSX } from "solid-js";
import { navigationRoutes } from "../../config/routes.js";
import { NavIconButton, TextButton } from "./Button.jsx";

export const LoggedInNavbar: Component = () => {
  // const tokenStorage = TokenStorageState.use();

  const right = (
    <div class="flex gap-3">
      <NavIconButton icon="i-lucide-settings" route={navigationRoutes.index} />
      {/* <IconButton icon="i-lucide-log-out" onClick={tokenStorage.logOut} /> */}
    </div>
  );

  return <BaseNavbar right={right} />;
};

export const LoggedOutNavbar: Component = () => <BaseNavbar />;

type BaseNavbarProps = {
  left?: JSX.Element;
  right?: JSX.Element;
};

const BaseNavbar: Component<BaseNavbarProps> = (props) => {
  const homeButton = (
    <A href={navigationRoutes.index}>
      <TextButton size="lg">Boards</TextButton>
    </A>
  );

  return (
    <div class="w-full border-b-2 border-zinc-700 px-2 py-2 flex flex-two justify-between items-center">
      <div class="w-max flex flex-row gap-3 items-center">
        {homeButton}
        {props.left}
      </div>
      <div class="w-max">{props.right}</div>
    </div>
  );
};
