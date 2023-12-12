import { createZodForm } from "@/lib/signals/form";
import { CreateChannelForm, TeamChannel, createChannelForm } from "@/models/channels";
import { reset } from "@modular-forms/solid";
import { Accessor, createMemo } from "solid-js";
import {
  channelsQuery,
  createChannelMutation,
  deleteChannelMuation,
} from "../api/channels";

export class ChannelSettingsState {
  static create = (teamSlug: Accessor<string>) => {
    return createMemo(() => new ChannelSettingsState(teamSlug()));
  };

  readonly createChannelForm = createZodForm(createChannelForm);

  private channelsQueries;
  private createChannelMutation;
  private deleteMutation;

  private constructor(teamSlug: string) {
    this.channelsQueries = channelsQuery({ teamSlug });
    this.createChannelMutation = createChannelMutation({
      teamSlug,
      onSuccess: () => {
        reset(this.createChannelForm);
      },
    });

    this.deleteMutation = deleteChannelMuation({ teamSlug });
  }

  get channels(): TeamChannel[] {
    return this.channelsQueries.data ?? [];
  }

  createChannel = (form: CreateChannelForm) => {
    this.createChannelMutation.mutate(form);
  };

  deleteChannel = (slug: string) => {
    this.deleteMutation.mutate(slug);
  };
}
