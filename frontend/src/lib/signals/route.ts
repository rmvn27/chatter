import type { RouteGuard } from "@/lib/route.types";
import { useNavigate, useParams } from "@solidjs/router";
import type { Accessor } from "solid-js";
import { createRenderEffect } from "solid-js";
import type { z } from "zod";

export const useZodParams = <Params extends z.AnyZodObject>(
  schema: Params,
): Accessor<z.infer<Params>> => {
  const params = useParams();

  return () => schema.parse(params);
};

export const useRouteGuard = (guardCreator: RouteGuard) => {
  const guard = guardCreator();
  const nav = useNavigate();

  // Run the effect as fast as possible
  // `createEffect` would wait until the rendering is done
  createRenderEffect(() => {
    const result = guard();
    if (result !== undefined && result.type === "replace") {
      nav(result.to, { replace: true });
    }
  });
};
