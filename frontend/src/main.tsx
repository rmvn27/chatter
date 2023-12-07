import { QueryClient } from "@tanstack/solid-query";
import { App } from "./components/App";
import { runApp } from "./lib/solid";

import "@unocss/reset/tailwind.css";
import "virtual:uno.css";
import { createServices } from "./services";

const services = createServices();
const queryClient = new QueryClient();
runApp(() => <App queryClient={queryClient} services={services} />);
