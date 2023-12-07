import type { RouteDefinition } from "@solidjs/router";
import type { Accessor, Component } from "solid-js";
import type { z } from "zod";

//
// GUARDS
//

type RouteGuardResult =
  | {
      type: "replace";
      to: string;
    }
  | {
      type: "keep";
    };

// When a string is returned, replace the current route with it
export type RouteGuard = () => Accessor<RouteGuardResult | undefined>;

//
// ROUTE DATA
//

type BaseRouteData = {
  guard?: RouteGuard;
};

type RouteDataWithParams<Params extends z.AnyZodObject> = BaseRouteData & {
  params: Params;
};

export type RouteData<Params extends z.AnyZodObject = z.AnyZodObject> =
  | BaseRouteData
  | RouteDataWithParams<Params>;

// Differentiate between passing a schema for the params and ommiting them
export type RouteDataFunction = {
  (def: BaseRouteData): BaseRouteData;
  <Params extends z.AnyZodObject>(
    def: RouteDataWithParams<Params>,
  ): RouteDataWithParams<Params>;
};

export type RouteComponent<Data extends RouteData = BaseRouteData> =
  Data extends RouteDataWithParams<infer Params>
    ? Component<{ params: z.infer<Params> }>
    : Component;

//
// IMPORTS
//

export type RouteImport<Data extends RouteData> = {
  Route: RouteComponent<Data>;
  routeData: Data;
};

type CreateRouteData<Data extends RouteData> = {
  path: string;
  component?: () => Promise<RouteImport<Data>>;
  children?: RouteDefinition[];
};

export type CreateRouteFunction = <Data extends RouteData>(
  data: CreateRouteData<Data>,
) => RouteDefinition;
