import { navigationRoutes } from "@/config/routes.js";
import { createZodForm } from "@/lib/signals/form.js";
import { authForm, type AuthForm } from "@/models/auth.js";
import { A } from "@solidjs/router";
import type { Component, JSX } from "solid-js";
import { FormCard } from "../form/FormCard.jsx";
import { FormErrorLabel } from "../form/FormLayout.jsx";
import { TextField } from "../form/TextField.jsx";
import { TextButton } from "../lib/Button.jsx";

type AuthCardProps = {
  error: string | undefined;
  onSubmit: (values: AuthForm) => void;
};

export const LoginCard: Component<AuthCardProps> = (props) => {
  return (
    <BaseAuthCard
      name="Login"
      submitLabel="LOGIN"
      onSubmit={props.onSubmit}
      error={props.error}
    >
      <A href={navigationRoutes.register} class="text-sky-300/80 hover:text-sky-300/50">
        No Account? Register instead!
      </A>
    </BaseAuthCard>
  );
};

export const RegisterCard: Component<AuthCardProps> = (props) => {
  return (
    <BaseAuthCard
      name="Register"
      submitLabel="REGISTER"
      onSubmit={props.onSubmit}
      error={props.error}
    />
  );
};

type BaseAuthCardProps = {
  name: string;
  submitLabel: string;
  onSubmit: (values: AuthForm) => void;
  children?: JSX.Element;
  error: string | undefined;
};

const BaseAuthCard: Component<BaseAuthCardProps> = (props) => {
  const form = createZodForm(authForm);

  const formActions = (
    <TextButton size="sm" style="highlight">
      {props.submitLabel}
    </TextButton>
  );

  return (
    <FormCard
      label={props.name}
      form={form}
      onSubmit={props.onSubmit}
      actions={formActions}
    >
      <TextField form={form} name={"username"} label={"Username"} />
      <TextField form={form} name={"password"} label={"Password"} type="password" />

      <FormErrorLabel value={props.error} />
      {props.children}
    </FormCard>
  );
};
