import { DeleteAccountCard } from "@/components/settings/DeleteAccounCard";
import { GeneralUserSettingsCard } from "@/components/settings/GeneralUserSettingsCard";
import { UpdatePasswordCard } from "@/components/settings/UpdatePasswordCard";
import { UserSettingsLayout } from "@/components/settings/UserSettingsLayout";
import { RouteComponent, RouteData } from "@/lib/route.types";
import { UserSettingsState } from "@/signals/userSettingsState";

export const routeData = {} satisfies RouteData;

export const Route: RouteComponent<typeof routeData> = () => {
  const state = UserSettingsState.create();

  return (
    <UserSettingsLayout>
      <GeneralUserSettingsCard state={state()} />
      <UpdatePasswordCard state={state()} />
      <DeleteAccountCard state={state()} />
    </UserSettingsLayout>
  );
};
