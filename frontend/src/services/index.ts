import { TokenService } from "./tokenService";

export type Services = {
  token: TokenService;
};

export const createServices = (): Services => {
  const token = TokenService.get();

  return { token };
};
