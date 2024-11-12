import {Component, EventEmitter, Input, Output} from '@angular/core';
import {DifficultyProfile} from "../../../model/difficulty-profile.model";
import {MaskService} from "../../../service/mask.service";
import {ServerDifficultyService} from "../../../service/server-difficulty.service";
import {NotificationService} from "../../../service/notification.service";
import {MatDialog} from "@angular/material/dialog";
import {
  DifficultyDeleteConfirmDialogComponent
} from "../difficulty-delete-confirm-dialog/difficulty-delete-confirm-dialog.component";

@Component({
  selector: 'app-difficulty-panel',
  templateUrl: './difficulty-panel.component.html',
  styleUrl: './difficulty-panel.component.scss'
})
export class DifficultyPanelComponent {
  @Input() difficultyProfile!: DifficultyProfile;
  @Output() activated = new EventEmitter<DifficultyProfile>();
  @Output() saved = new EventEmitter<DifficultyProfile>();
  @Output() deleted = new EventEmitter<DifficultyProfile>();

  editingTitle: boolean = false;

  constructor(private maskService: MaskService,
              private difficultyService: ServerDifficultyService,
              private notificationService: NotificationService,
              private matDialog: MatDialog) {
  }

  toggleActive(event: MouseEvent) {
    event.stopPropagation();
    this.activated.emit(this.difficultyProfile);
  }

  allowDecimals(event: KeyboardEvent) {
    return this.allowDigits(event) || event.key === ".";
  }

  allowDigits(event: KeyboardEvent) {
    const number = event.key as unknown as number;
    return number >= 0 && number <= 9 || this.isInputAllowedKey(event.key);
  }

  isInputAllowedKey(key: string) {
    return key === 'Backspace' || key === 'ArrowLeft' || key === 'ArrowRight' || key === 'Tab' || key === 'Delete';
  }

  deleteDifficulty(difficultyProfile: DifficultyProfile) {
    const dialogRef = this.matDialog.open(DifficultyDeleteConfirmDialogComponent, {
      width: '250px',
      enterAnimationDuration: '200ms',
      exitAnimationDuration: '200ms'
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        let identifier = difficultyProfile.id !== undefined ? difficultyProfile.id : difficultyProfile.name;
        this.maskService.show();
        this.difficultyService.deleteDifficulty(identifier).subscribe(response => {
          this.maskService.hide();
          this.notificationService.successNotification("Difficulty profile has been deleted");
          this.deleted.emit(difficultyProfile);
        });
      }
    });
  }

  saveDifficulty(difficultyProfile: DifficultyProfile) {
    this.maskService.show();
    this.difficultyService.saveDifficulties([difficultyProfile]).subscribe(response => {
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
    if (event.code === 'Enter') {
      this.editingTitle = false;
    }
  }
}
