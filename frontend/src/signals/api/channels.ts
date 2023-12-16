import { apiPaths } from "@/config/api";
import { mutationKeys, queryKeys } from "@/config/query";
import { deleteJson, getJson, patchJson, postJson } from "@/lib/fetch";
import {
  MutationFn,
  QueryFn,
  createBaseMutation,
  createBaseQuery,
  createQueryInvalidation,
  withOnSuccess,
} from "@/lib/signals/query";
import {
  Channel,
  CreateChannelForm,
  UpdateChannelForm,
  channel,
} from "@/models/channels";
import { z } from "zod";

type TeamSlugProps = { teamSlug: string };

const createChannelsInvalidation = (teamSlug: string) =>
  createQueryInvalidation(queryKeys.channels(teamSlug));

export const channelsQuery: QueryFn<Channel[], TeamSlugProps> = ({
  onSuccess,
  teamSlug,
}) => {
  const queryFn = () => getJson(apiPaths.channels(teamSlug).base, channel.array());

  return createBaseQuery({
    onSuccess,
    queryFn,
    queryKey: queryKeys.channels(teamSlug),
  });
};

export const createChannelMutation: MutationFn<
  CreateChannelForm,
  unknown,
  TeamSlugProps
> = ({ onSuccess, teamSlug }) => {
  const invalidate = createChannelsInvalidation(teamSlug);
  const mutationFn = (form: CreateChannelForm) =>
    postJson(apiPaths.channels(teamSlug).base, z.unknown(), form);

  return createBaseMutation({
    onSuccess: withOnSuccess(onSuccess, invalidate),
    mutationFn,
    mutationKey: mutationKeys.channels.create,
  });
};

export const updateChannelMutation: MutationFn<
  UpdateChannelForm,
  unknown,
  TeamSlugProps & { channelSlug: string }
> = ({ onSuccess, teamSlug, channelSlug }) => {
  const invalidate = createChannelsInvalidation(teamSlug);
  const mutationFn = (form: UpdateChannelForm) =>
    patchJson(apiPaths.channels(teamSlug).withSlug(channelSlug), z.unknown(), form);

  return createBaseMutation({
    onSuccess: withOnSuccess(onSuccess, invalidate),
    mutationFn,
    mutationKey: mutationKeys.channels.update,
  });
};

export const deleteChannelMuation: MutationFn<string, unknown, TeamSlugProps> = ({
  onSuccess,
  teamSlug,
}) => {
  const invalidate = createChannelsInvalidation(teamSlug);
  const mutationFn = (slug: string) =>
    deleteJson(apiPaths.channels(teamSlug).withSlug(slug), z.unknown());

  return createBaseMutation({
    onSuccess: withOnSuccess(onSuccess, invalidate),
    mutationFn,
    mutationKey: mutationKeys.channels.delete,
  });
};
