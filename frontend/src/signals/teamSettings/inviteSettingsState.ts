import { TeamInvite } from "@/models/teams";
import { Accessor, createMemo } from "solid-js";
import {
  createInviteMutation,
  deleteInviteMuation,
  invitesQuery,
} from "../api/invites";

export class InviteSettingsState {
  static create = (teamSlug: Accessor<string>) => {
    return createMemo(() => new InviteSettingsState(teamSlug()));
  };

  private readonly invitesQuery;
  private readonly createMutation;
  private readonly deleteMutation;

  private constructor(teamSlug: string) {
    this.invitesQuery = invitesQuery({ teamSlug });
    this.createMutation = createInviteMutation({ teamSlug });
    this.deleteMutation = deleteInviteMuation({ teamSlug });
  }

  get invites(): TeamInvite[] {
    return this.invitesQuery.data ?? [];
  }

  create = () => {
    this.createMutation.mutate(undefined);
  };

  delete = (invite: string) => {
    this.deleteMutation.mutate(invite);
  };
}
