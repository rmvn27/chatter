import type { UserSettingsState } from "@/signals/userSettingsState";
import type { Component } from "solid-js";
import { Card } from "../layout/Card";
import { TextButton } from "../lib/Button";
import { DeleteAccountModal } from "./DeleteAccountModal";

type Props = {
  state: UserSettingsState;
};

export const DeleteAccountCard: Component<Props> = (props) => {
  const deleteToggle = () => props.state.deleteToggle;

  return (
    <>
      <Card label="Delete Account">
        <p class="text-md text-zinc-400">
          Do you really want to delete this account? Every team will be deleted! Invited
          participants won't have access to them anymore!
        </p>
        <div class="w-full flex flex-row justify-end items-center">
          <TextButton style="neg" size="sm" onClick={deleteToggle().toggle}>
            DELETE
          </TextButton>
        </div>
      </Card>

      <DeleteAccountModal
        toggle={deleteToggle()}
        onDelete={props.state.deleteAccount}
      />
    </>
  );
};
