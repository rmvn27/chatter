import { apiPaths } from "@/config/api";
import { mutationKeys, queryKeys } from "@/config/query";
import { deleteJson, getJson, postJson } from "@/lib/fetch";
import {
  MutationFn,
  QueryFn,
  createBaseMutation,
  createBaseQuery,
  createQueryInvalidation,
  withOnSuccess,
} from "@/lib/signals/query";
import { TeamInvite, teamInvite } from "@/models/teams";
import { z } from "zod";

type TeamSlugProps = { teamSlug: string };

const createInvitesInvalidation = (teamSlug: string) =>
  createQueryInvalidation(queryKeys.invites(teamSlug));

export const invitesQuery: QueryFn<TeamInvite[], TeamSlugProps> = ({
  onSuccess,
  teamSlug,
}) => {
  const queryFn = async () => {
    const data = await getJson(apiPaths.invites(teamSlug).base, teamInvite.array());

    // prepend the team slug for later api access
    // and easier ux for the user
    return data.map((d) => `${teamSlug}@${d}`);
  };

  return createBaseQuery({
    onSuccess,
    queryFn,
    queryKey: queryKeys.invites(teamSlug),
  });
};

export const createInviteMutation: MutationFn<unknown, unknown, TeamSlugProps> = ({
  onSuccess,
  teamSlug,
}) => {
  const invalidate = createInvitesInvalidation(teamSlug);
  const mutationFn = () => postJson(apiPaths.invites(teamSlug).base, z.unknown(), {});

  return createBaseMutation({
    onSuccess: withOnSuccess(onSuccess, invalidate),
    mutationFn,
    mutationKey: mutationKeys.invites.create,
  });
};

export const deleteInviteMuation: MutationFn<string, unknown, TeamSlugProps> = ({
  onSuccess,
  teamSlug,
}) => {
  const invalidate = createInvitesInvalidation(teamSlug);
  const mutationFn = (id: string) => {
    const [, invite] = id.split("@");
    return deleteJson(apiPaths.invites(teamSlug).withId(invite as string), z.unknown());
  };

  return createBaseMutation({
    onSuccess: withOnSuccess(onSuccess, invalidate),
    mutationFn,
    mutationKey: mutationKeys.invites.delete,
  });
};
