var PROXY_CONFIG = {
  "/api": {
    "target": "http://localhost:8085",
    "secure": false,
    "logLevel": "debug",
    "changeOrigin": true
  }
}

module.exports = PROXY_CONFIG;
