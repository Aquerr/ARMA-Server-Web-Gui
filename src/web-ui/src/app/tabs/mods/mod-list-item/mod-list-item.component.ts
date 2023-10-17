import {Component, EventEmitter, Input, Output} from "@angular/core";
import {Mod} from "../../../model/mod.model";
import {ModDeleteConfirmDialogComponent} from "../mod-delete-confirm-dialog/mod-delete-confirm-dialog.component";
import {MatDialog} from "@angular/material/dialog";
import {MaskService} from "../../../service/mask.service";
import {ServerModsService} from "../../../service/server-mods.service";

@Component({
  selector: '[app-mod-list-item]',
  templateUrl: './mod-list-item.component.html',
  styleUrls: ['./mod-list-item.component.css']
})
export class ModListItemComponent {

  @Input() mod!: Mod;
  @Output() onModDelete: EventEmitter<Mod> = new EventEmitter<Mod>();

  constructor(private matDialog: MatDialog,
              private maskService: MaskService,
              private modService: ServerModsService) {}

  showModDeleteConfirmationDialog(modName: string) {
    const dialogRef = this.matDialog.open(ModDeleteConfirmDialogComponent, {
      width: '250px',
      enterAnimationDuration: '200ms',
      exitAnimationDuration: '200ms'
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        this.deleteMod(modName);
      }
    });
  }

  deleteMod(modName: string) {
    this.maskService.show();
    this.modService.deleteMod(modName).subscribe(response => {
      this.maskService.hide();
      this.onModDelete.next(this.mod);
    });
  }
}
