import { ChangeDetectionStrategy, Component, model, output } from "@angular/core";
import { DifficultyProfile } from "../../../model/difficulty-profile.model";
import { LoadingSpinnerMaskService } from "../../../service/loading-spinner-mask.service";
import { ServerDifficultyService } from "../../../service/server-difficulty.service";
import { NotificationService } from "../../../service/notification.service";
import { MatDialog } from "@angular/material/dialog";
import {
  DifficultyDeleteConfirmDialogComponent
} from "../difficulty-delete-confirm-dialog/difficulty-delete-confirm-dialog.component";
import {
  MatAccordion,
  MatExpansionPanel,
  MatExpansionPanelDescription,
  MatExpansionPanelHeader,
  MatExpansionPanelTitle
} from "@angular/material/expansion";
import { MatFormField, MatInput, MatLabel } from "@angular/material/input";
import { FormsModule } from "@angular/forms";
import { NgClass } from "@angular/common";
import { MatIcon } from "@angular/material/icon";
import { MatOption, MatSelect } from "@angular/material/select";
import { MatCheckbox } from "@angular/material/checkbox";
import { MatButton, MatIconButton } from "@angular/material/button";

@Component({
  selector: "app-difficulty-panel",
  templateUrl: "./difficulty-panel.component.html",
  imports: [
    MatAccordion,
    MatExpansionPanel,
    MatExpansionPanelHeader,
    MatExpansionPanelTitle,
    MatInput,
    FormsModule,
    NgClass,
    MatExpansionPanelDescription,
    MatIcon,
    MatFormField,
    MatLabel,
    MatSelect,
    MatOption,
    MatCheckbox,
    MatButton,
    MatIconButton
  ],
  styleUrl: "./difficulty-panel.component.scss",
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class DifficultyPanelComponent {
  difficultyProfile = model.required<DifficultyProfile>();

  activated = output<DifficultyProfile>();
  saved = output<DifficultyProfile>();
  deleted = output<DifficultyProfile>();

  editingTitle: boolean = false;

  constructor(
    private maskService: LoadingSpinnerMaskService,
    private difficultyService: ServerDifficultyService,
    private notificationService: NotificationService,
    private matDialog: MatDialog
  ) {}

  toggleActive(event: MouseEvent) {
    event.stopPropagation();
    this.difficultyProfile.update((profile) => {
      profile.active = !profile.active;
      return profile;
    });
    this.activated.emit(this.difficultyProfile());
  }

  allowDecimals(event: KeyboardEvent) {
    return this.allowDigits(event) || event.key === ".";
  }

  allowDigits(event: KeyboardEvent) {
    const number = event.key as unknown as number;
    return (number >= 0 && number <= 9) || this.isInputAllowedKey(event.key);
  }

  isInputAllowedKey(key: string) {
    return (
      key === "Backspace"
      || key === "ArrowLeft"
      || key === "ArrowRight"
      || key === "Tab"
      || key === "Delete"
    );
  }

  deleteDifficulty(difficultyProfile: DifficultyProfile) {
    const dialogRef = this.matDialog.open(DifficultyDeleteConfirmDialogComponent, {
      width: "250px",
      enterAnimationDuration: "200ms",
      exitAnimationDuration: "200ms"
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        const identifier
          = difficultyProfile.id ?? difficultyProfile.name;
        this.maskService.show();
        this.difficultyService.deleteDifficulty(identifier).subscribe(() => {
          this.maskService.hide();
          this.notificationService.successNotification("Difficulty profile has been deleted");
          this.deleted.emit(difficultyProfile);
        });
      }
    });
  }

  saveDifficulty(difficultyProfile: DifficultyProfile) {
    this.maskService.show();
    this.difficultyService.saveDifficulties([difficultyProfile]).subscribe(() => {
      this.maskService.hide();
      this.notificationService.successNotification("Difficulty has been saved");
      this.saved.emit(difficultyProfile);
    });
  }

  editTitle(event: MouseEvent) {
    event.stopPropagation();
    this.editingTitle = !this.editingTitle;
  }

  onTitleKeyDown(event: KeyboardEvent) {
    event.stopPropagation();
    if (event.code === "Enter") {
      this.editingTitle = false;
    }
  }
}
