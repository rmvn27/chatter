import { lazy, type Component } from "solid-js";
import { Dynamic } from "solid-js/web";
import type { CreateRouteFunction, RouteData, RouteImport } from "./route.types";
import { useRouteGuard, useZodParams } from "./signals/route";

export const createRoute: CreateRouteFunction = ({ path, component, children }) => ({
  path,
  component: component ? lazyLoadRoute(component) : undefined,
  children,
});

const lazyLoadRoute = <Data extends RouteData>(
  importer: () => Promise<RouteImport<Data>>,
) => lazy(() => importer().then((i) => ({ default: createRouteComponent(i) })));

export const createRouteComponent = <Data extends RouteData>({
  routeData,
  Route,
}: RouteImport<Data>): Component => {
  return () => {
    if (routeData.guard !== undefined) {
      useRouteGuard(routeData.guard);
    }

    if ("params" in routeData) {
      const params = useZodParams(routeData.params);
      return (
        <Dynamic
          component={Route as Component<{ params: Record<string, unknown> }>}
          params={params()}
        />
      );
    } else {
      return <Dynamic component={Route as Component} />;
    }
  };
};
