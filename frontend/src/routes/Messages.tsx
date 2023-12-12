import { RouteComponent, RouteData } from "@/lib/route.types";
import { z } from "zod";

export const routeData = {
  params: z.object({
    teamSlug: z.string(),
    channelSlug: z.string(),
  }),
} satisfies RouteData;

export const Route: RouteComponent<typeof routeData> = () => {
  return <></>;
};
