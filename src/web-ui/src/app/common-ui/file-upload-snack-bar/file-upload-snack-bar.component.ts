import { Component, Inject, OnInit } from "@angular/core";
import { MAT_SNACK_BAR_DATA, MatSnackBarRef } from "@angular/material/snack-bar";
import { FileUploadService } from "../../service/file-upload.service";
import { MatProgressBar } from "@angular/material/progress-bar";
import { FilesizePipe } from "../../util/filesize.pipe";
import { MatLabel } from "@angular/material/form-field";
import { MatIcon } from "@angular/material/icon";

@Component({
  selector: "app-file-upload-snack-bar",
  templateUrl: "./file-upload-snack-bar.component.html",
  imports: [MatLabel, MatIcon, MatProgressBar, FilesizePipe],
  styleUrls: ["./file-upload-snack-bar.component.scss"]
})
export class FileUploadSnackBarComponent implements OnInit {
  constructor(
    public snackBarRef: MatSnackBarRef<FileUploadSnackBarComponent>,
    @Inject(MAT_SNACK_BAR_DATA) public fileUploadService: FileUploadService
  ) {}

  ngOnInit(): void {}

  getUploadingFiles() {
    return this.fileUploadService.getUploadingFiles();
  }
}
