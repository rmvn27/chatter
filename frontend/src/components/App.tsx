import { appRoutes } from "@/config/routes";
import type { Services } from "@/services";
import { ServicesProvider } from "@/signals/services";
import { Router, useRoutes } from "@solidjs/router";
import { QueryClient, QueryClientProvider } from "@tanstack/solid-query";
import type { Component, ParentComponent } from "solid-js";

type Props = {
  services: Services;
  queryClient: QueryClient;
};

export const App: Component<Props> = (props) => {
  const Routes = useRoutes(appRoutes);

  return (
    <AppProviders queryClient={props.queryClient} services={props.services}>
      <Router>
        <Routes />
      </Router>
    </AppProviders>
  );
};

const AppProviders: ParentComponent<Props> = (props) => {
  return (
    <ServicesProvider value={props.services}>
      <QueryClientProvider client={props.queryClient}>
        {props.children}
      </QueryClientProvider>
    </ServicesProvider>
  );
};
