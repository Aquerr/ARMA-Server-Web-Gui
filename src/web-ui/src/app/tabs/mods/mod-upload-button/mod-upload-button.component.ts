import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { MaskService } from 'src/app/service/mask.service';
import { ServerModsService } from 'src/app/service/server-mods.service';
import {ModUploadService} from "../service/mod-upload.service";

@Component({
  selector: 'app-upload-mod',
  templateUrl: './mod-upload-button.component.html',
  styleUrls: ['./mod-upload-button.component.css']
})
export class ModUploadButtonComponent implements OnInit {

  @Output() modUploaded: EventEmitter<any> = new EventEmitter<any>();

  constructor(private maskService: MaskService,
              private modService: ServerModsService,
              private modUploadService: ModUploadService) { }

  ngOnInit(): void {
  }

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
