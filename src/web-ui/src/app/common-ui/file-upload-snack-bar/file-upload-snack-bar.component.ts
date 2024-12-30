import {Component, Inject, OnInit} from '@angular/core';
import {MAT_SNACK_BAR_DATA, MatSnackBarRef} from "@angular/material/snack-bar";
import {FileUploadService} from "../../service/file-upload.service";

@Component({
    selector: 'app-file-upload-snack-bar',
    templateUrl: './file-upload-snack-bar.component.html',
    styleUrls: ['./file-upload-snack-bar.component.scss'],
    standalone: false
})
export class FileUploadSnackBarComponent implements OnInit {

  constructor(public snackBarRef: MatSnackBarRef<FileUploadSnackBarComponent>,
              @Inject(MAT_SNACK_BAR_DATA) public fileUploadService: FileUploadService) { }

  ngOnInit(): void {
  }

  getUploadingFiles() {
    return this.fileUploadService.getUploadingFiles();
  }
}
