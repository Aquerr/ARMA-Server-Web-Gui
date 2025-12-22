import { Injectable, signal } from "@angular/core";

@Injectable({
  providedIn: "root"
})
export class LoadingSpinnerMaskService {

  public spinnerVisible = signal(false);

  constructor() {}

  show(): void {
    this.spinnerVisible.set(true);
  }

  hide(): void {
    this.spinnerVisible.set(false);
  }
}
