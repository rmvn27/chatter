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
  CreateTeamForm,
  JoinTeamForm,
  Team,
  UpdateTeamForm,
  team,
} from "@/models/teams";
import { z } from "zod";

type TeamSlugProps = {
  teamSlug: string;
};

export const teamsQuery: QueryFn<Team[]> = ({ onSuccess }) => {
  const queryFn = () => getJson(apiPaths.teams.base, team.array());

  return createBaseQuery({
    onSuccess,
    queryFn,
    queryKey: queryKeys.teams.all,
  });
};

export const teamQuery: QueryFn<Team, TeamSlugProps> = ({ onSuccess, teamSlug }) => {
  const queryFn = () => getJson(apiPaths.teams.bySlug(teamSlug).details, team);

  return createBaseQuery({
    onSuccess,
    queryFn,
    queryKey: queryKeys.teams.bySlug(teamSlug).base,
  });
};

export const createTeamMutation: MutationFn<CreateTeamForm, unknown> = ({
  onSuccess,
}) => {
  const invalidate = createQueryInvalidation(queryKeys.teams.all);

  const mutationFn = (form: CreateTeamForm) =>
    postJson(apiPaths.teams.base, z.unknown(), form);

  return createBaseMutation({
    onSuccess: withOnSuccess(onSuccess, invalidate),
    mutationFn,
    mutationKey: mutationKeys.teams.create,
  });
};

export const joinTeamMutation: MutationFn<JoinTeamForm, unknown> = ({ onSuccess }) => {
  const invalidate = createQueryInvalidation(queryKeys.teams.all);

  const mutationFn = (form: JoinTeamForm) => {
    const [teamSlug, invite] = form.invite.split("@");

    return postJson(apiPaths.teams.bySlug(teamSlug as string).join, z.unknown(), {
      invite,
    });
  };

  return createBaseMutation({
    onSuccess: withOnSuccess(onSuccess, invalidate),
    mutationFn,
    mutationKey: mutationKeys.teams.join,
  });
};

export const leaveTeamMutation: MutationFn<undefined, unknown, TeamSlugProps> = ({
  onSuccess,
  teamSlug,
}) => {
  const invalidate = createQueryInvalidation(queryKeys.teams.all);

  const mutationFn = () =>
    postJson(apiPaths.teams.bySlug(teamSlug).leave, z.unknown(), {});

  return createBaseMutation({
    onSuccess: withOnSuccess(onSuccess, invalidate),
    mutationFn,
    mutationKey: mutationKeys.teams.leave,
  });
};

export const updateTeamMutation: MutationFn<UpdateTeamForm, unknown, TeamSlugProps> = ({
  teamSlug,
  onSuccess,
}) => {
  const invalidate = createQueryInvalidation(queryKeys.teams.all);

  const mutationFn = (form: UpdateTeamForm) =>
    patchJson(apiPaths.teams.bySlug(teamSlug).details, z.unknown(), form);

  return createBaseMutation({
    onSuccess: withOnSuccess(onSuccess, invalidate),
    mutationFn,
    mutationKey: mutationKeys.teams.update,
  });
};

export const deleteTeamMutation: MutationFn<undefined, unknown, TeamSlugProps> = ({
  teamSlug,
  onSuccess,
}) => {
  const invalidate = createQueryInvalidation(queryKeys.teams.all);

  const mutationFn = () =>
    deleteJson(apiPaths.teams.bySlug(teamSlug).details, z.unknown());

  return createBaseMutation({
    onSuccess: withOnSuccess(onSuccess, invalidate),
    mutationFn,
    mutationKey: mutationKeys.teams.delete,
  });
};
