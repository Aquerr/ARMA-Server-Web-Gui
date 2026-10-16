import { Injectable } from "@angular/core";
import { ModPreset } from "@model/mod.model";

@Injectable({
  providedIn: "root"
})
export class ModPresetHtmlGeneratorService {
  public generateHtmlFile(modPreset: ModPreset): string {
    const htmlHeader = `<?xml version="1.0" encoding="utf-8"?>
<html>
  <!-- Created by ASWG - Arma Server Web GUI -->
  <head>
    <meta name="arma:Type" content="preset" />
    <meta name="arma:PresetName" content="${modPreset.name}" />
    <meta name="generator" content="ASWG" />
    <title>Arma 3</title>
    <link href="https://fonts.googleapis.com/css?family=Roboto" rel="stylesheet" type="text/css" />
    <style>
body {
\tmargin: 0;
\tpadding: 0;
\tcolor: #fff;
\tbackground: #000;\t
}

body, th, td {
\tfont: 95%/1.3 Roboto, Segoe UI, Tahoma, Arial, Helvetica, sans-serif;
}

td {
    padding: 3px 30px 3px 0;
}

h1 {
    padding: 20px 20px 0 20px;
    color: white;
    font-weight: 200;
    font-family: segoe ui;
    font-size: 3em;
    margin: 0;
}

em {
    font-variant: italic;
    color:silver;
}

.before-list {
    padding: 5px 20px 10px 20px;
}

.mod-list {
    background: #222222;
    padding: 20px;
}

.dlc-list {
    background: #222222;
    padding: 20px;
}

.footer {
    padding: 20px;
    color:gray;
}

.whups {
    color:gray;
}

a {
    color: #D18F21;
    text-decoration: underline;
}

a:hover {
    color:#F1AF41;
    text-decoration: none;
}

.from-steam {
    color: #449EBD;
}
.from-local {
    color: gray;
}

</style></head>`;

    return htmlHeader + this.generateBody(modPreset) + "</html>";
  }

  private generateBody(modPreset: ModPreset): string {
    return `<body>
<h1>Arma 3  - Preset <strong>${modPreset.name}</strong></h1>
    <p class="before-list">
      <em>To import this preset, drag this file onto the Launcher window. Or click the MODS tab, then PRESET in the top right, then IMPORT at the bottom, and finally select this file.</em>
    </p>
    <div class="mod-list">
<table>` + this.generateModContainers(modPreset) + "</table></div></body>";
  }

  private generateModContainers(modPreset: ModPreset): string {
    let modContainersHtml = "";

    // TODO: Until we provide a way for adding local mods we are hardcoding "form-steam" = Steam here.
    for (const entry of modPreset.entries) {
      modContainersHtml = modContainersHtml + `<tr data-type="ModContainer">
          <td data-type="DisplayName">${entry.name}</td>
          <td>
            <span class="from-steam">Steam</span>
          </td>
          <td>
            <a href="https://steamcommunity.com/sharedfiles/filedetails/?id=${entry.modId}" data-type="Link">https://steamcommunity.com/sharedfiles/filedetails/?id=${entry.modId}</a>
          </td>
        </tr>`;
    }

    return modContainersHtml;
  }
}
