import { Component, OnInit } from '@angular/core';
import {WorkshopService} from "../service/workshop.service";
import {AuthService} from "../service/auth.service";

@Component({
  selector: 'app-side-menu',
  templateUrl: './side-menu.component.html',
  styleUrls: ['./side-menu.component.css']
})
export class SideMenuComponent implements OnInit {

  isWorkshopActive: boolean = false;

  constructor(private authService: AuthService,
              private workshopService: WorkshopService) {

    if (this.authService.isAuthenticated()) {
      this.workshopService.canUseWorkshop().subscribe(isWorkshopActive => {
        this.isWorkshopActive = isWorkshopActive;
      })
    }
  }

  ngOnInit(): void {
  }

  canUseWorkshop(): boolean {
    return this.isWorkshopActive;
  }
}
