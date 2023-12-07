import { z } from "zod";

export const nonEmptyString = (message?: string) => z.string().min(1, message);
