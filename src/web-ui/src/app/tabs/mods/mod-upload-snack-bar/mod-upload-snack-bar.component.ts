import {Component, inject, OnInit} from '@angular/core';
import {ModUploadService} from "../service/mod-upload.service";
import {MatSnackBarRef} from "@angular/material/snack-bar";

@Component({
  selector: 'app-mod-upload-snack-bar',
  templateUrl: './mod-upload-snack-bar.component.html',
  styleUrls: ['./mod-upload-snack-bar.component.css']
})
export class ModUploadSnackBarComponent implements OnInit {

  snackBarRef = inject(MatSnackBarRef);

  constructor(private modUploadService: ModUploadService) { }

  ngOnInit(): void {
  }

  getUploadingMods() {
    return this.modUploadService.getUploadingMods();
  }
}
