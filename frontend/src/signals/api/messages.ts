import { apiPaths } from "@/config/api";
import { queryKeys } from "@/config/query";
import { ApplicationError, getJson } from "@/lib/fetch";
import { TeamMessage, teamMessage } from "@/models/messages";
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

    const baseUrl = apiPaths.teams.bySlug(teamSlug).channels.messages(channelSlug);
    const timestamp = pageParam ?? new Date().getTime();

    // use a page size big enough to trigger the intersection observer after the
    // first load. otherwise we are stuck and then to refetching will happen
    const data = await getJson(
      `${baseUrl}?timestamp=${timestamp}&pageSize=30`,
      teamMessage.array(),
    );

    return data;
  };

  const getNextPageParam = (messages: TeamMessage[]) => {
    if (messages.length === 0) return undefined;

    return messages[messages.length - 1]?.timestamp;
  };

  return createInfiniteQuery<unknown, ApplicationError, TeamMessage[]>({
    queryFn,
    queryKey: queryKeys.teams.bySlug(teamSlug).messages(channelSlug),
    // typings are a bit wrong so we need to make it right
    // eslint-disable-next-line @typescript-eslint/no-explicit-any
    getNextPageParam: getNextPageParam as any,
  });
};
