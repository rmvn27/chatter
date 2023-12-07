import type { ParentComponent } from "solid-js";
import { ScreenLayout } from "../layout/ScreenLayout";
import { LoggedOutNavbar } from "../lib/Navbar";

export const AuthLayout: ParentComponent = (props) => {
  const navBar = <LoggedOutNavbar />;

  return (
    <ScreenLayout navbar={navBar}>
      <div class="mx-auto max-w-md pt-20">{props.children}</div>
    </ScreenLayout>
  );
};
