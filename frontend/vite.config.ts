import {
  presetIcons,
  presetUno,
  toEscapedSelector,
  transformerDirectives,
} from "unocss";
import uno, { type VitePluginConfig } from "unocss/vite";
import { fileURLToPath } from "url";
import { defineConfig, type ServerOptions } from "vite";
import solidPlugin from "vite-plugin-solid";

export default defineConfig(() => ({
  plugins: [solidPlugin(), uno(createUnoConfig())],
  resolve: {
    alias: {
      "@": fileURLToPath(new URL("./src", import.meta.url)),
    },
  },
  server: serverConfig(),
}));

const createUnoConfig = (): VitePluginConfig => {
  return {
    transformers: [transformerDirectives()],
    presets: [presetUno(), presetIcons()],
    rules: [
      [
        /hide-scrollbar/,
        (_, { rawSelector }) => {
          const selector = toEscapedSelector(rawSelector);

          return `
            ${selector}::-webkit-scrollbar {
              display: none;
            }
            @-moz-document url-prefix() {
              ${selector} {
                scrollbar-width: none;
              }
            }
          `;
        },
      ],
    ],
  };
};

const serverConfig = (): ServerOptions => {
  return {
    proxy: {
      "/api": {
        target: "http://127.0.0.1:8080",
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, ""),
      },
    },
  };
};
