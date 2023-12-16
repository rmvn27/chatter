import { createZodForm } from "@/lib/signals/form";
import { SendMessageForm, TeamMessage, sendMessageForm } from "@/models/messages";
import { WebsocketService } from "@/services/websocketService";
import {
  Accessor,
  Setter,
  createEffect,
  createMemo,
  createSignal,
  onCleanup,
} from "solid-js";
import { messagesInfiniteQuery } from "./api/messages";
import { participantsQuery } from "./api/participants";
import { useServices } from "./services";

export class MessagesState {
  static create = (teamSlug: Accessor<string>, channelSlug: Accessor<string>) => {
    const services = useServices();

    return createMemo(() => new MessagesState(services.ws, teamSlug(), channelSlug()));
  };

  readonly sendMessageForm = createZodForm(sendMessageForm);

  readonly usernamesById: Accessor<Map<string, string>>;

  readonly liveMessages: Accessor<TeamMessage[]>;
  private readonly setLiveMessages: Setter<TeamMessage[]>;

  readonly messagesQuery;
  constructor(
    private readonly wsService: WebsocketService,
    teamSlug: string,
    channelSlug: string,
  ) {
    const participants = participantsQuery({ teamSlug });
    this.usernamesById = createMemo(() => {
      const output = new Map<string, string>();
      if (participants.data === undefined) return output;

      participants.data.forEach((d) => output.set(d.id, d.name));
      return output;
    });
    const [liveMessages, setLiveMessages] = createSignal<TeamMessage[]>([], {
      equals: false,
    });
    this.liveMessages = liveMessages;
    this.setLiveMessages = setLiveMessages;

    this.messagesQuery = messagesInfiniteQuery({ teamSlug, channelSlug });
  }

  sendMessage = (form: SendMessageForm) => {
    if (form.message === "") return;

    this.wsService.send({
      type: "sendTextMessage",
      message: form.message,
    });
    this.sendMessageForm.internal.fields.get("message")?.setInput("");
  };

  syncMessages = () => {
    createEffect(() => {
      const symb = this.wsService.registerListener((e) => {
        console.log(e);
        if (e.type === "message") {
          // add the new message and then just return the old array
          // dont create a copy for performance reasons
          //
          // also we need to prepend items to work with the column-reverse mode of
          // flexbox. Its needed to always show the most recent message without having to scroll
          //
          // otherwise prepending would have been more performant
          this.setLiveMessages((prev) => {
            prev.unshift(e.message);
            return prev;
          });
        }
      });

      onCleanup(() => {
        this.wsService.removeListener(symb);
      });
    });
  };

  createDisplayMessages = (messages: Accessor<TeamMessage[]>) => {
    return createMemo(() => {
      const usernamesMap = this.usernamesById();

      return messages().map((m) => {
        const date = new Date(m.timestamp);
        const hours = `${date.getHours()}`.padStart(2, "0");
        const minutes = `${date.getMinutes()}`.padStart(2, "0");
        const formattedDate = `${hours}:${minutes}`;

        const username =
          m.userId !== undefined ? usernamesMap.get(m.userId) : undefined;

        return {
          content: m.content,
          date: formattedDate,
          username: username ?? "<unknown>",
        };
      });
    });
  };
}
