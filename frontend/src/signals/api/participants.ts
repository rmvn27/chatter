import { apiPaths } from "@/config/api";
import { mutationKeys, queryKeys } from "@/config/query";
import { deleteJson, getJson } from "@/lib/fetch";
import {
  MutationFn,
  QueryFn,
  createBaseMutation,
  createBaseQuery,
  createQueryInvalidation,
  withOnSuccess,
} from "@/lib/signals/query";
import { Participant, participant } from "@/models/teams";
import { z } from "zod";

type TeamSlugProps = { teamSlug: string };

export const participantsQuery: QueryFn<Participant[], TeamSlugProps> = ({
  onSuccess,
  teamSlug,
}) => {
  const queryFn = () =>
    getJson(apiPaths.participants(teamSlug).base, participant.array());

  return createBaseQuery({
    onSuccess,
    queryFn,
    queryKey: queryKeys.participants(teamSlug),
  });
};

type RemoveParticipantData = {
  teamSlug: string;
};

export const removeParticipant: MutationFn<
  Participant,
  unknown,
  RemoveParticipantData
> = ({ onSuccess, teamSlug }) => {
  const invalidate = createQueryInvalidation(queryKeys.participants(teamSlug));

  const mutationFn = (participant: Participant) =>
    deleteJson(apiPaths.participants(teamSlug).withId(participant.id), z.unknown());

  return createBaseMutation({
    onSuccess: withOnSuccess(onSuccess, invalidate),
    mutationFn,
    mutationKey: mutationKeys.participants.delete,
  });
};
