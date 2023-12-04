import { presetIcons, presetUno, transformerDirectives } from "unocss";
import uno, { type VitePluginConfig } from "unocss/vite";
import { fileURLToPath } from "url";
import { defineConfig } from "vite";
import solidPlugin from "vite-plugin-solid";

export default defineConfig(() => ({
  plugins: [solidPlugin(), uno(createUnoConfig())],
  resolve: {
    alias: {
      "@": fileURLToPath(new URL("./src", import.meta.url)),
    },
  },
}));

const createUnoConfig = (): VitePluginConfig => {
  return {
    transformers: [transformerDirectives()],
    presets: [presetUno(), presetIcons()],
  };
};
