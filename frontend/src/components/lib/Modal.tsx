import type { ToggleSignal } from "@/lib/signals/toggle";
import { Show, type Component, type JSXElement } from "solid-js";
import { Portal } from "solid-js/web";

type Props = {
  opened: ToggleSignal;
  onClose?: () => void;
  children: JSXElement;
};

export const Modal: Component<Props> = (props) => {
  return (
    <BaseModal opened={props.opened} onClose={props.onClose}>
      <div class="pb-40">
        <div class="w-lg" onClick={(e) => e.stopPropagation()}>
          {props.children}
        </div>
      </div>
    </BaseModal>
  );
};

// Is exported for modals that need more control
// for example in the styling
export const BaseModal: Component<Props> = (props) => {
  return (
    <Show when={props.opened.value()}>
      <Portal>
        <div
          class="w-full h-full flex flex-col justify-center items-center fixed top-0 left-0 bg-zinc-900/25"
          onClick={() => {
            props.opened.toggle();
            props.onClose?.();
          }}
        >
          {props.children}
        </div>
      </Portal>
    </Show>
  );
};
