const { defineConfig, configDefaults} = require("vitest/config");
const { currentPath } = require("path");

module.exports = defineConfig({
  ...configDefaults,
  test: {
    environment: 'jsdom',
    coverage: {
      provider: 'v8',
    },
    reporters: ['html'],
    outputFile: currentPath.join(__dirname, "./coverage/arma-web-gui")
  }
});
