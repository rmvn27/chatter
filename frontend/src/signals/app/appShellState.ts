import { queryKeys } from "@/config/query";
import { navigationRoutes } from "@/config/routes";
import { ToggleSignal, createToggle } from "@/lib/signals/toggle";
import { Team } from "@/models/teams";
import { TokenService } from "@/services/tokenService";
import { WebsocketService } from "@/services/websocketService";
import { Navigator, useNavigate } from "@solidjs/router";
import { QueryClient, useQueryClient } from "@tanstack/solid-query";
import { Accessor, createMemo, onCleanup } from "solid-js";
import { teamsQuery } from "../api/teams";
import { useServices } from "../services";
import { LocationChangeTracker } from "./locationChangeTracker";

type AppRouteData = {
  teamSlug?: string | undefined;
  channelSlug?: string | undefined;
};

export class AppShellState {
  static create = (routeData: Accessor<AppRouteData>) => {
    const services = useServices();
    const queryClient = useQueryClient();
    const nav = useNavigate();

    const tracker = createMemo(() => new LocationChangeTracker(services.ws));

    return createMemo(
      () =>
        new AppShellState(
          services.token,
          services.ws,
          tracker(),
          queryClient,
          nav,
          routeData,
        ),
    );
  };

  private readonly teamsQuery;

  readonly addTeamModalToggle: ToggleSignal;
  readonly routeData: Accessor<AppRouteData>;
  readonly teamSlug: Accessor<string | undefined>;

  private constructor(
    private readonly tokenService: TokenService,
    private readonly wsService: WebsocketService,

    private readonly locationChangeTracker: LocationChangeTracker,

    private readonly queryClient: QueryClient,
    private readonly nav: Navigator,
    routeData: Accessor<AppRouteData>,
  ) {
    this.addTeamModalToggle = createToggle();

    this.routeData = createMemo(() => routeData());
    this.teamSlug = () => this.routeData().teamSlug;

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

  // NOTE should be called withing a effect!
  notifyOnTeamAndChannelChange = () => {
    this.locationChangeTracker.changeLocation(this.routeData());
  };

  // hook into the change events sent by the backend and invalidate the appropriate query caches
  // as of now we do this for the participants and channels
  //
  // NOTE should be called withing a effect!
  syncQueryCache = () => {
    const sym = this.wsService.registerListener((e) => {
      if (e.type === "participantListChanged") {
        this.queryClient.invalidateQueries({
          queryKey: queryKeys.participants(e.teamSlug)(),
        });
      } else if (e.type === "channelListChanged") {
        this.queryClient.invalidateQueries({
          queryKey: queryKeys.channels(e.teamSlug)(),
        });
      }
    });

    onCleanup(() => this.wsService.removeListener(sym));
  };
}
