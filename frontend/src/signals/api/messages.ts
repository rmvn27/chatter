import { apiPaths } from "@/config/api";
import { queryKeys } from "@/config/query";
import { ApplicationError, getJson } from "@/lib/fetch";
import { Message, message } from "@/models/messages";
import { createInfiniteQuery } from "@tanstack/solid-query";

type MessagesQueryProps = {
  teamSlug: string;
  channelSlug: string;
};

export const messagesInfiniteQuery = ({
  teamSlug,
  channelSlug,
}: MessagesQueryProps) => {
  const queryFn = async (params: unknown) => {
    const { pageParam } = params as { pageParam?: number };

    const baseUrl = apiPaths.messages(teamSlug, channelSlug);
    const timestamp = pageParam ?? new Date().getTime();

    // use a page size big enough to trigger the intersection observer after the
    // first load. otherwise we are stuck and then to refetching will happen
    const data = await getJson(
      `${baseUrl}?timestamp=${timestamp}&pageSize=30`,
      message.array(),
    );

    return data;
  };

  // get the next page param from the last timestamp of the fetched messages
  const getNextPageParam = (messages: Message[]) => {
    if (messages.length === 0) return undefined;

    return messages[messages.length - 1]?.timestamp;
  };

  return createInfiniteQuery<unknown, ApplicationError, Message[]>({
    queryFn,
    queryKey: queryKeys.messages(teamSlug, channelSlug),
    // typings are a bit wrong so we need to make it right
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    getNextPageParam: getNextPageParam as any,
  });
};
