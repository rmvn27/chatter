import type { ParentComponent } from "solid-js";
import { ScreenLayout } from "../layout/ScreenLayout";

export const AuthLayout: ParentComponent = (props) => {
  return (
    <ScreenLayout>
      <div class="mx-auto max-w-md pt-20 flex flex-col gap-8">{props.children}</div>
    </ScreenLayout>
  );
};
