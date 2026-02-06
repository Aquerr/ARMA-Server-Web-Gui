import { Component, Inject, signal, WritableSignal } from "@angular/core";
import { MatButton } from "@angular/material/button";
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogClose,
  MatDialogContent,
  MatDialogTitle
} from "@angular/material/dialog";
import { ModDependency } from "../../../service/workshop.service";

@Component({
  selector: "app-workshop-install-dependencies-dialog",
  templateUrl: "./workshop-install-dependencies-dialog.component.html",
  imports: [
    MatButton,
    MatDialogActions,
    MatDialogClose,
    MatDialogContent,
    MatDialogTitle
  ],
  styleUrls: ["./workshop-install-dependencies-dialog.component.css"]
})
export class WorkshopInstallDependenciesDialogComponent {
  private dependencies: WritableSignal<ModDependency[]> = signal<ModDependency[]>([]);
  public dependenciesReadOnly = this.dependencies.asReadonly();

  constructor(@Inject(MAT_DIALOG_DATA) dependencies: ModDependency[]) {
    this.dependencies.set(dependencies);
  }
}
