import { Injectable } from "@angular/core";
import { ModPreset, ModPresetEntry } from "../../../../model/mod.model";

@Injectable({
  providedIn: "root"
})
export class ModPresetParserService {
  constructor() {}

  processModPresetFile(result: string | ArrayBuffer | null): ModPreset | null {
    if (typeof result !== "string") {
      console.error("Could not process file due to bad type...");
      return null;
    }

    const html = document.createElement("html");
    html.innerHTML = result;
    const table = html.getElementsByClassName("mod-list")[0].firstElementChild as HTMLTableElement;

    const mods: ModPresetEntry[] = [];
    for (let i = 0; i < table.rows.length; i++) {
      const row = table.rows[i];
      const modId = this.getModIdFromRow(row);
      const modTitle = this.getModTitleFromRow(row);
      if (modId != null && modTitle != null) {
        mods.push({ id: modId, name: modTitle });
      }
    }
    const presetName =
      html.querySelector('meta[name="arma:PresetName"]')?.getAttribute("content") ?? "custom";

    return { name: presetName, entries: mods };
  }

  private getModTitleFromRow(row: HTMLTableRowElement): string | null {
    for (let i = 0; i < row.cells.length; i++) {
      const cell = row.cells[i];
      if (cell.getAttribute("data-type") == "DisplayName") {
        const modTitle = cell.innerText;
        return modTitle;
      }
    }
    return null;
  }

  private getModIdFromRow(row: HTMLTableRowElement): number | null {
    for (let i = 0; i < row.cells.length; i++) {
      const cell = row.cells[i];
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
