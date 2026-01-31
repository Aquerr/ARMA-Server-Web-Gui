import { Injectable } from "@angular/core";
import { ModPreset, ModPresetEntry } from "../../../../model/mod.model";

@Injectable({
  providedIn: "root"
})
export class ModPresetParserService {
  processModPresetFile(fileContents: string | ArrayBuffer | null): ModPreset | null {
    if (typeof fileContents !== "string") {
      console.error("Could not process file due to bad type...");
      return null;
    }

    const html = document.createElement("html");
    html.innerHTML = fileContents;
    const table = html.getElementsByClassName("mod-list")[0].firstElementChild as HTMLTableElement;

    const mods: ModPresetEntry[] = [];
    for (const row of table.rows) {
      const modId = this.getModIdFromRow(row);
      const modTitle = this.getModTitleFromRow(row);
      if (modId != null && modTitle != null) {
        mods.push({ id: modId, name: modTitle });
      }
    }
    const presetName
      = html.querySelector("meta[name=\"arma:PresetName\"]")?.getAttribute("content");

    return { name: presetName, entries: mods };
  }

  private getModTitleFromRow(row: HTMLTableRowElement): string | null {
    for (const cell of row.cells) {
      if (cell.getAttribute("data-type") == "DisplayName") {
        return cell.innerText;
      }
    }
    return null;
  }

  private getModIdFromRow(row: HTMLTableRowElement): number | null {
    for (const cell of row.cells) {
      const linkElement = cell.firstElementChild;
      if (linkElement != null && linkElement.getAttribute("data-type") == "Link") {
        const href = linkElement.getAttribute("href");
        if (href == null) return null;
        const id = href.substring(href.lastIndexOf("?id=") + 4, href.length);
        return Number(id);
      }
    }
    return null;
  }
}
