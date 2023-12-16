import { SettingsLayout } from "@/components/lib/SettingsLayout";
import { DeleteAccountCard } from "@/components/settings/DeleteAccounCard";
import { GeneralUserSettingsCard } from "@/components/settings/GeneralUserSettingsCard";
import { UpdatePasswordCard } from "@/components/settings/UpdatePasswordCard";
import { RouteComponent, RouteData } from "@/lib/route.types";
import { UserSettingsState } from "@/signals/userSettingsState";

export const routeData = {} satisfies RouteData;

export const Route: RouteComponent<typeof routeData> = () => {
  const state = UserSettingsState.create();

  return (
    <SettingsLayout title="User - Settings">
      <GeneralUserSettingsCard state={state()} />
      <UpdatePasswordCard state={state()} />
      <DeleteAccountCard state={state()} />
    </SettingsLayout>
  );
};
