import { inject, Injectable } from "@angular/core";
import { MatIconRegistry } from "@angular/material/icon";
import { DomSanitizer } from "@angular/platform-browser";

@Injectable({
  providedIn: "root"
})
export class IconRegistrarService {

  private matIconRegistry: MatIconRegistry = inject(MatIconRegistry)
  private domSanitizer: DomSanitizer = inject(DomSanitizer);

  constructor() {
    this.matIconRegistry.addSvgIcon("aswg-steam", this.domSanitizer.bypassSecurityTrustResourceUrl("/assets/icon/svg/steam.svg"));
  }
}
