import { defineConfig, configDefaults } from "vitest/config";

export default defineConfig({
  test: {
    ...configDefaults,
    exclude: [
      ...configDefaults.exclude,
      "testing/**/**"
    ],
    globals: true,
    environment: "jsdom",
    coverage: {
      provider: "v8",
      exclude: [
        "testing/**/**"
      ]
    },
    reporters: ["verbose"],
    outputFile: "./coverage/arma-web-gui"
  }
});
