import { ToggleSignal, createToggle } from "@/lib/signals/toggle";
import { Team } from "@/models/teams";
import { TokenService } from "@/services/tokenService";
import { QueryClient, useQueryClient } from "@tanstack/solid-query";
import { Accessor, createMemo } from "solid-js";
import { teamsQuery } from "../api/teams";
import { useServices } from "../services";

export class AppShellState {
  static create = (teamSlug: Accessor<string | undefined>) => {
    const services = useServices();
    const queryClient = useQueryClient();

    return createMemo(() => new AppShellState(services.token, queryClient, teamSlug));
  };

  private teamsQuery: ReturnType<typeof teamsQuery>;
  public addTeamModalToggle: ToggleSignal;
  public teamSlug: Accessor<string | undefined>;
  private constructor(
    private tokenService: TokenService,
    private queryClient: QueryClient,
    teamSlug: Accessor<string | undefined>,
  ) {
    this.addTeamModalToggle = createToggle();
    this.teamSlug = createMemo(() => teamSlug());

    this.teamsQuery = teamsQuery({});
  }

  get teams(): Team[] {
    return this.teamsQuery.data ?? [];
  }

  logout = async () => {
    await this.tokenService.logOut();
    this.queryClient.clear();
  };
}
