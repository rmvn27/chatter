import { ChatInput } from "@/components/messages/ChatInput";
import { ChatMessages } from "@/components/messages/ChatMessages";
import { MessagesLayout } from "@/components/messages/MessagesLayout";
import { RouteComponent, RouteData } from "@/lib/route.types";
import { MessagesState } from "@/signals/messagesState";
import { createEffect } from "solid-js";
import { z } from "zod";

export const routeData = {
  params: z.object({ teamSlug: z.string(), channelSlug: z.string() }),
} satisfies RouteData;

export const Route: RouteComponent<typeof routeData> = (props) => {
  const teamSlug = () => props.params.teamSlug;
  const channelSlug = () => props.params.channelSlug;

  const state = MessagesState.create(teamSlug, channelSlug);

  createEffect(() => {
    const s = state();
    s.syncMessages();
  });

  const messages = <ChatMessages state={state()} />;
  const chatInput = <ChatInput state={state()} />;

  return <MessagesLayout messages={messages} chatInput={chatInput} />;
};
