import { Component } from '@angular/core';
import {ModUploadService} from "../service/mod-upload.service";

@Component({
    selector: 'app-upload-mod',
    templateUrl: './mod-upload-button.component.html',
    styleUrls: ['./mod-upload-button.component.scss'],
    standalone: false
})
export class ModUploadButtonComponent {

  constructor(private modUploadService: ModUploadService) { }

  onFileSelected(event: Event) {
    const target = (event.target as HTMLInputElement);

    if (!target.files)
      return;

    const file : File = target.files[0];
    if (file)
    {
      this.modUploadService.uploadMod(file);
    }
  }
}
