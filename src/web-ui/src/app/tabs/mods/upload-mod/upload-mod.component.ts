import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { MaskService } from 'src/app/service/mask.service';
import { NotificationService } from 'src/app/service/notification.service';
import { ServerModsService } from 'src/app/service/server-mods.service';

@Component({
  selector: 'app-upload-mod',
  templateUrl: './upload-mod.component.html',
  styleUrls: ['./upload-mod.component.css']
})
export class UploadModComponent implements OnInit {

  @Output() modUploaded: EventEmitter<any> = new EventEmitter<any>();

  constructor(private maskService: MaskService,
              private modService: ServerModsService,
              private notificationService: NotificationService) { }

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
      this.uploadFile(file);
    }
  }

  uploadFile(file: File) {
    const formData = new FormData();
    formData.append("file", file);

    this.maskService.show();
    this.modService.uploadMission(formData).subscribe((response) => {
        if (response.type == 4)
        {
          this.maskService.hide();
          this.modUploaded.emit();
          this.notificationService.successNotification("Mod has been uploaded!");
        }
      },
      (error) => {
        this.maskService.hide();
        this.notificationService.errorNotification(error.error.message);
      });
  }

}
