import { ToggleSignal } from "@/lib/signals/toggle";
import { CreateTeamForm, JoinTeamForm } from "@/models/teams";
import { createMemo } from "solid-js";
import { createTeamMutation, joinTeamMutation } from "../api/teams";

export class AddTeamState {
  static create = (toggleSignal: ToggleSignal) => {
    return createMemo(() => new AddTeamState(toggleSignal));
  };

  private createMuation;
  private joinMutation;

  private constructor(private readonly toggleSignal: ToggleSignal) {
    this.createMuation = createTeamMutation({
      onSuccess: () => {
        this.toggleSignal.set(false);
      },
    });

    this.joinMutation = joinTeamMutation({
      onSuccess: () => {
        this.toggleSignal.set(false);
      },
    });
  }

  createNewTeam = (data: CreateTeamForm) => this.createMuation.mutate(data);
  joinTeam = (data: JoinTeamForm) => this.joinMutation.mutate(data);
}
