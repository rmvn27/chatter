import type { Component } from "solid-js";
import { Dynamic, render } from "solid-js/web";

export const runApp = (App: Component) =>
  // Root is always defined
  // eslint-disable-next-line @typescript-eslint/no-non-null-assertion
  render(() => <Dynamic component={App} />, document.getElementById("app")!);
