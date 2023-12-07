import { createForm, zodForm } from "@modular-forms/solid";
import type { z } from "zod";

export const createZodForm = <Schema extends z.ZodTypeAny>(schema: Schema) =>
  createForm<z.infer<Schema>>({ validate: zodForm(schema) });
