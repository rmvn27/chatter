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
  CreateChannelForm,
  TeamChannel,
  UpdateChannelForm,
  teamChannel,
} from "@/models/channels";
import { z } from "zod";

type TeamSlugProps = {
  teamSlug: string;
};

export const channelsQuery: QueryFn<TeamChannel[], TeamSlugProps> = ({
  onSuccess,
  teamSlug,
}) => {
  const queryFn = () =>
    getJson(apiPaths.teams.bySlug(teamSlug).channels.base, teamChannel.array());

  return createBaseQuery({
    onSuccess,
    queryFn,
    queryKey: queryKeys.teams.bySlug(teamSlug).channels,
  });
};

export const createChannelMutation: MutationFn<
  CreateChannelForm,
  unknown,
  TeamSlugProps
> = ({ onSuccess, teamSlug }) => {
  const invalidate = createQueryInvalidation(queryKeys.teams.bySlug(teamSlug).channels);

  const mutationFn = (form: CreateChannelForm) =>
    postJson(apiPaths.teams.bySlug(teamSlug).channels.base, z.unknown(), form);

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
  const invalidate = createQueryInvalidation(queryKeys.teams.bySlug(teamSlug).channels);

  const mutationFn = (form: UpdateChannelForm) =>
    patchJson(
      apiPaths.teams.bySlug(teamSlug).channels.withSlug(channelSlug),
      z.unknown(),
      form,
    );

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
  const invalidate = createQueryInvalidation(queryKeys.teams.bySlug(teamSlug).channels);

  const mutationFn = (slug: string) =>
    deleteJson(apiPaths.teams.bySlug(teamSlug).channels.withSlug(slug), z.unknown());

  return createBaseMutation({
    onSuccess: withOnSuccess(onSuccess, invalidate),
    mutationFn,
    mutationKey: mutationKeys.channels.delete,
  });
};
