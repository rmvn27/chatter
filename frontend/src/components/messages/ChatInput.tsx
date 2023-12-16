import { MessagesState } from "@/signals/messagesState";
import { Form } from "@modular-forms/solid";
import { Component, createSignal } from "solid-js";
import { TextArea } from "../form/TextArea";

type Props = {
  state: MessagesState;
};

export const ChatInput: Component<Props> = (props) => {
  const form = () => props.state.sendMessageForm;
  const [formRef, setFormRef] = createSignal<Element | undefined>(undefined);
  const submit = () => {
    const event = new Event("submit", { cancelable: false });
    formRef()?.dispatchEvent(event);
  };

  // since we can't submit the form via the enter key like in a normal input field
  // we have to to this manually with the ref
  return (
    <Form of={form()} onSubmit={props.state.sendMessage} ref={setFormRef}>
      <TextArea
        form={form()}
        name="message"
        placeholder="Message..."
        onEnter={submit}
      />
    </Form>
  );
};
