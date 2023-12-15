import { TokenService } from "./tokenService";
import { WebsocketService } from "./websocketService";

export type Services = {
  token: TokenService;
  ws: WebsocketService;
};

export const createServices = (): Services => {
  const ws = new WebsocketService();
  const token = new TokenService(ws);
  TokenService.set(token);

  return { token, ws };
};
