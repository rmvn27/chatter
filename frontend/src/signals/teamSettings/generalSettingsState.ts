import { navigationRoutes } from "@/config/routes";
import { createZodForm } from "@/lib/signals/form";
import { createToggle } from "@/lib/signals/toggle";
import { UpdateTeamForm, updateTeamForm } from "@/models/teams";
import { useNavigate, type Navigator } from "@solidjs/router";
import { createMemo, type Accessor } from "solid-js";
import { deleteTeamMutation, teamQuery, updateTeamMutation } from "../api/teams";

export class GeneralTeamSettingsState {
  static create = (teamSlug: Accessor<string>) => {
    const nav = useNavigate();
    return createMemo(() => new GeneralTeamSettingsState(teamSlug(), nav));
  };

  readonly updateTeamForm = createZodForm(updateTeamForm);
  readonly deleteToggle = createToggle();

  private readonly updateMutation;
  private readonly deleteMutation;

  constructor(teamSlug: string, navigate: Navigator) {
    // when we have the name of the team avaiable
    // set it directly in the form
    //
    // this should be near instantaneos since we already have it cached
    teamQuery({
      teamSlug,
      onSuccess: ({ name }) => {
        this.updateTeamForm.internal.fields.get("name")?.setInput(name);
      },
    });

    this.updateMutation = updateTeamMutation({ teamSlug });
    this.deleteMutation = deleteTeamMutation({
      teamSlug,
      onSuccess: () => navigate(navigationRoutes.index),
    });
  }

  updateTeam = (data: UpdateTeamForm) => {
    this.updateMutation.mutate(data);
  };

  deleteTeam = () => {
    this.deleteMutation.mutate(undefined);
  };
}
