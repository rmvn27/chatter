import { createRequiredContext } from "@/lib/signals/context";
import type { Services } from "@/services";

export const { Provider: ServicesProvider, useContext: useServices } =
  createRequiredContext<Services>("Services");
