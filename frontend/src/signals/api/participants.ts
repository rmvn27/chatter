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
import { TeamParticipant, teamParticipant } from "@/models/teams";
import { z } from "zod";

type TeamSlugProps = {
  teamSlug: string;
};
export const participantsQuery: QueryFn<TeamParticipant[], TeamSlugProps> = ({
  onSuccess,
  teamSlug,
}) => {
  const queryFn = () =>
    getJson(apiPaths.teams.bySlug(teamSlug).participants.base, teamParticipant.array());

  return createBaseQuery({
    onSuccess,
    queryFn,
    queryKey: queryKeys.teams.bySlug(teamSlug).participants,
  });
};

type RemoveParticipantData = {
  teamSlug: string;
};

export const removeParticipant: MutationFn<
  TeamParticipant,
  unknown,
  RemoveParticipantData
> = ({ onSuccess, teamSlug }) => {
  const invalidate = createQueryInvalidation(
    queryKeys.teams.bySlug(teamSlug).participants,
  );

  const mutationFn = (participant: TeamParticipant) =>
    deleteJson(
      apiPaths.teams.bySlug(teamSlug).participants.withId(participant.id),
      z.unknown(),
    );

  return createBaseMutation({
    onSuccess: withOnSuccess(onSuccess, invalidate),
    mutationFn,
    mutationKey: mutationKeys.participants.delete,
  });
};
