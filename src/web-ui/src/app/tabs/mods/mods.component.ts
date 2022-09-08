import { Component, OnInit, ViewChild } from '@angular/core';
import { Subject } from 'rxjs';
import { MaskService } from 'src/app/service/mask.service';
import { ServerModsService } from 'src/app/service/server-mods.service';
import { UploadModComponent } from './upload-mod/upload-mod.component';

@Component({
  selector: 'app-mods',
  templateUrl: './mods.component.html',
  styleUrls: ['./mods.component.css']
})
export class ModsComponent implements OnInit {

  @ViewChild('uploadMod') uploadModComponent!: UploadModComponent;

  reloadModsDataSubject: Subject<any>;

  mods: string[] = [];

  constructor(private modService: ServerModsService, private maskService: MaskService) {this.reloadModsDataSubject = new Subject(); }

  ngOnInit(): void {
    this.maskService.show();
    this.modService.getInstalledMods().subscribe(modsResponse => {
      this.mods = modsResponse.mods;
      this.maskService.hide();
    });
  }

  onModUploaded() {
    this.reloadModsDataSubject.next(null);
  }
}