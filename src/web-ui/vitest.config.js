const { defineConfig, configDefaults} = require("vitest/config");

module.exports = defineConfig({
  ...configDefaults,
  test: {
    environment: 'jsdom',
    coverage: {
      provider: 'v8',
    },
    reporters: ['verbose'],
    outputFile: "./coverage/arma-web-gui"
  }
});
