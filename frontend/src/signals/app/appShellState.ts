import { navigationRoutes } from "@/config/routes";
import { ToggleSignal, createToggle } from "@/lib/signals/toggle";
import { Team } from "@/models/teams";
import { TokenService } from "@/services/tokenService";
import { Navigator, useNavigate } from "@solidjs/router";
import { QueryClient, useQueryClient } from "@tanstack/solid-query";
import { Accessor, createEffect, createMemo } from "solid-js";
import { teamsQuery } from "../api/teams";
import { useServices } from "../services";
import { LocationChangeTracker } from "./locationChangeTracker";

type AppRouteData = {
  teamSlug?: string | undefined;
  channelSlug?: string | undefined;
};

export class AppShellState {
  static create = (teamSlug: Accessor<string | undefined>) => {
    const services = useServices();
    const queryClient = useQueryClient();
    const nav = useNavigate();

    const tracker = createMemo(() => new LocationChangeTracker(services.ws));

    return createMemo(
      () => new AppShellState(services.token, tracker(), queryClient, nav, teamSlug),
    );
  };

  private readonly teamsQuery;

  readonly addTeamModalToggle: ToggleSignal;
  readonly teamSlug: Accessor<string | undefined>;

  private constructor(
    private readonly tokenService: TokenService,
    private readonly locationChangeTracker: LocationChangeTracker,

    private readonly queryClient: QueryClient,
    private readonly nav: Navigator,
    teamSlug: Accessor<string | undefined>,
  ) {
    this.addTeamModalToggle = createToggle();
    this.teamSlug = createMemo(() => teamSlug());

    this.teamsQuery = teamsQuery({});
  }

  get teams(): Team[] {
    return this.teamsQuery.data ?? [];
  }

  navToSettings = () => {
    this.nav(navigationRoutes.settings);
  };

  logout = async () => {
    await this.tokenService.logOut();
    this.queryClient.clear();
  };

  notifyOnTeamAndChannelChange = (routeData: Accessor<AppRouteData>) => {
    const teamSlug = createMemo(() => routeData().teamSlug);
    const channelSlug = createMemo(() => routeData().channelSlug);

    createEffect(() => {
      this.locationChangeTracker.changeLocation(teamSlug(), channelSlug());
    });
  };
}
