import { MessagesState } from "@/signals/messagesState";
import { Form } from "@modular-forms/solid";
import { Component } from "solid-js";
import { TextField } from "../form/TextField";

type Props = {
  state: MessagesState;
};

export const ChatInput: Component<Props> = (props) => {
  const form = () => props.state.sendMessageForm;

  return (
    <Form of={form()} onSubmit={props.state.sendMessage}>
      <TextField form={form()} name="message" placeholder="Message..." />
    </Form>
  );
};
