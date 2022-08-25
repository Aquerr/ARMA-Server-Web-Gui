import { Component, OnInit } from '@angular/core';
import { MaskService } from 'src/app/service/mask.service';
import { ServerModsService } from 'src/app/service/server-mods.service';

@Component({
  selector: 'app-mods',
  templateUrl: './mods.component.html',
  styleUrls: ['./mods.component.css']
})
export class ModsComponent implements OnInit {

  mods: string[] = [];

  constructor(private modService: ServerModsService, private maskService: MaskService) { }

  ngOnInit(): void {
    this.maskService.show();
    this.modService.getInstalledMods().subscribe(modsResponse => {
      this.mods = modsResponse.mods;
      this.maskService.hide();
    });
  }
}