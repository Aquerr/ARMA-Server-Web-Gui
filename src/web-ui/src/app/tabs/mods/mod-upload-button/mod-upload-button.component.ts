import { Component } from '@angular/core';
import {ModUploadService} from "../service/mod-upload.service";

@Component({
  selector: 'app-upload-mod',
  templateUrl: './mod-upload-button.component.html',
  styleUrls: ['./mod-upload-button.component.scss']
})
export class ModUploadButtonComponent {

  constructor(private modUploadService: ModUploadService) { }

  onFileSelected(event: Event) {
    console.log(event);

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
