import { appRoutes } from "@/config/routes";
import { createServices, type Services } from "@/services";
import { ServicesProvider } from "@/signals/services";
import { Router, useRoutes } from "@solidjs/router";
import { QueryClient, QueryClientProvider } from "@tanstack/solid-query";
import {
  createEffect,
  createMemo,
  type Component,
  type ParentComponent,
} from "solid-js";

export const App: Component = () => {
  const services = createMemo(() => createServices());
  const queryClient = createMemo(() => new QueryClient());

  createEffect(() => {
    services().token.setupTokens();
  });

  const Routes = useRoutes(appRoutes);

  return (
    <AppProviders queryClient={queryClient()} services={services()}>
      <Router>
        <Routes />
      </Router>
    </AppProviders>
  );
};

type ProviderProps = {
  services: Services;
  queryClient: QueryClient;
};

const AppProviders: ParentComponent<ProviderProps> = (props) => {
  return (
    <ServicesProvider value={props.services}>
      <QueryClientProvider client={props.queryClient}>
        {props.children}
      </QueryClientProvider>
    </ServicesProvider>
  );
};
