import { WsCommand, WsEvent, wsCommand, wsEvent } from "@/models/ws";

type MessageListener = (message: WsEvent) => void;

export class WebsocketService {
  // Used to store messages that can't be send, when the ws is connecting
  // will be flushed when the connection is set up
  private startupQueue: string[] = [];
  private readonly listeners = new Map<symbol, MessageListener>();

  private instance: WebSocket | undefined = undefined;
  constructor() {}

  send = (cmd: WsCommand) => {
    const rawData = JSON.stringify(wsCommand.parse(cmd));
    console.log(cmd);

    // Send when connected
    if (this.instance !== undefined && this.instance.readyState === 1) {
      this.instance.send(rawData);
    } else {
      this.startupQueue.push(rawData);
    }
  };

  registerListener = (listener: MessageListener): symbol => {
    const token = Symbol("wsListener");
    this.listeners.set(token, listener);

    return token;
  };

  removeListener = (token: symbol) => {
    this.listeners.delete(token);
  };

  // since we need to authenticate first
  // we explicitly connect to the ws instance
  connect = (token: string) => {
    const location = window.location;
    const proto = location.protocol === "https" ? "wss" : "ws";
    const host = location.host;

    this.instance = new WebSocket(`${proto}://${host}/api/ws`);
    this.instance.addEventListener("open", () => {
      // authenticate before flushing more data
      this.send({ type: "authenticate", token });

      this.flushData();
    });
    this.instance.addEventListener("message", (m) => {
      this.onMessage(m.data);
    });
  };

  disconnect = async () => {
    if (this.instance === undefined) return;

    await new Promise((resolve) => {
      this.instance?.addEventListener("close", resolve);
      this.instance?.close();
      this.instance = undefined;
    });
  };

  // by now we should have a instance
  private flushData = () => {
    if (this.instance !== undefined && this.instance.readyState !== 1) return;

    this.startupQueue.forEach((data) => {
      this.instance?.send(data);
    });

    this.startupQueue = [];
  };

  private onMessage = (rawData: string) => {
    const data = wsEvent.parse(JSON.parse(rawData));

    this.listeners.forEach((l) => l(data));
  };
}
