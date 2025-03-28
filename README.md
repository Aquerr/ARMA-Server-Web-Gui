# ASWG - Arma Server Web Gui

![License](https://img.shields.io/github/license/aquerr/arma-server-web-gui.svg?label=License)
[![Discord](https://img.shields.io/discord/447076657698963466.svg?color=blue&label=Discord&logo=Discord&logoColor=white)](https://discord.gg/Zg3rWta)

## General
ASWG is a very user-friendly management gui for Arma 3 servers.

It is a web application that was built with a goal to provide easy way to manage Arma server located on Linux systems (however it supports Windows as well!).

You can easily start/stop your server, view server console, change server configuration `server.cfg`, install missions and manage mods. Also, it allows you to automatically download and install workshop mods via SteamCMD.

Check the feature list below to get to know more.

**If you find any issues while using **ASWG** or you want to share your thoughts on what could be added, post them at [Github](https://github.com/Aquerr/arma-server-web-gui/issues)**

**If you enjoy using ASWG, give this repo a star!**

## Features

* Fast setup
* Windows support
* Linux support (currently tested on Ubuntu)
* Server Console + Player list
* Editing of server configuration
* Mods management
* Mission management
* Workshop and mods installation via SteamCMD (manual installation required for SteamCMD)

### Screenshots

![Status](https://i.imgur.com/9URalf9.png)
![Mods](https://i.imgur.com/uT9LHXY.png)
![Workshop](https://i.imgur.com/eqypD2G.png)
![Light theme](https://i.imgur.com/CX36nzu.png)
![Configuration](https://i.imgur.com/6CqEexJ.png)

# Setup

### Installation:
- Install JRE 21
- [Install Arma Server in desired directory](https://community.bistudio.com/wiki/Arma_3:_Dedicated_Server).
- [Download](https://github.com/Aquerr/ARMA-Server-Web-Gui/packages/2322633) ASWG from Github Packages or [build ASWG yourself](#Building).
- Put `ASWG.jar` file in desired folder where you want your ASWG to be running.
- Run `ASWG.jar` by executing `java -jar aswg.jar` in the console.
  - To change `ASWG` port, for example to `8444`. Run it with `java -Dserver.port=8444 -jar aswg.jar`
- ASWG configuration file will be created after on first run. Edit it to set the ASWG username and password. Restart ASWG after making changes.
- Open `http://localhost:8085` to enter ASWG.
- Extra: If you want to use steam workshop and download mods automatically, configure steamcmd properties.

For SteamCMD installation check [SteamCMD wiki](https://developer.valvesoftware.com/wiki/SteamCMD).

# Building

- Install JDK 21
- Clone repo
- Go to project directory
- Run `./mvnw.cmd clean package`
- The `ASWG.jar` artifact will be located inside `target` directory.

## Credits / Thanks

Many thanks to:
- **[mateo9x](https://github.com/mateo9x)** (for help with dark theme and some front-end things)

Also thanks to JetBrains for their IDE

<img width="200" src="https://resources.jetbrains.com/storage/products/company/brand/logos/jetbrains.png" alt="JetBrains logo.">

## License

[Apache License 2.0](https://github.com/Aquerr/ARMA-Server-Web-Gui/blob/main/LICENSE)

## Donation

Creation of this project is really a time-consuming task. If you would like to support me then you can star this repo or send me some cookies through [PayPal](https://paypal.me/aquerrnerdi).