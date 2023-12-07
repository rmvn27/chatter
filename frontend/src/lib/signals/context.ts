import type { FlowComponent } from "solid-js";
import { createContext, useContext as useInternalContext } from "solid-js";

// Solid doesn't export this type
export type ContextProviderComponent<T> = FlowComponent<{
  value: T;
}>;

type RequiredContext<Ctx> = {
  Provider: ContextProviderComponent<Ctx | undefined>;
  useContext: () => Ctx;
};

export const createRequiredContext = <Ctx>(name: string): RequiredContext<Ctx> => {
  const Context = createContext<Ctx>();

  const useContext = () => {
    const ctx = useInternalContext(Context);
    if (ctx === undefined) throw new Error(`Missig Context: ${name}`);

    return ctx;
  };

  return {
    Provider: Context.Provider,
    useContext,
  };
};
