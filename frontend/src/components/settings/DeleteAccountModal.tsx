import { ToggleSignal } from "@/lib/signals/toggle";
import type { Component } from "solid-js";
import { Card } from "../layout/Card";
import { TextButton } from "../lib/Button";
import { Modal } from "../lib/Modal";

type Props = {
  toggle: ToggleSignal;
  onDelete: () => void;
};

export const DeleteAccountModal: Component<Props> = (props) => {
  return (
    <Modal opened={props.toggle}>
      <Card label="Delete Account" class="bg-zinc-800">
        <p class="text-md text-zinc-400">Are you sure?</p>

        <div class="flex flex-row gap-3 justify-end items-center">
          <TextButton
            style="neg"
            size="sm"
            onClick={() => {
              props.toggle.toggle();
              props.onDelete();
            }}
          >
            DELETE
          </TextButton>

          <TextButton style="pos" size="sm" onClick={props.toggle.toggle}>
            CANCEL
          </TextButton>
        </div>
      </Card>
    </Modal>
  );
};
