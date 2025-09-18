#!/bin/sh
echo "[INIT] Setting permissions for /aswg/data and /aswg/config"
chown -R 1001:1001 /aswg/data /aswg/config

java -jar /aswg/aswg.jar