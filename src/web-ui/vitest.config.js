const { defineConfig, configDefaults} = require("vitest/config");

module.exports = defineConfig({
  ...configDefaults,
  test: {
    exclude: [
      ...configDefaults.exclude,
      "./testing/**"
    ],
    environment: 'jsdom',
    coverage: {
      provider: 'v8',
    },
    reporters: ['verbose'],
    outputFile: "./coverage/arma-web-gui"
  }
});
