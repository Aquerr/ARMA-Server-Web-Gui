import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {WorkshopService} from "../service/workshop.service";
import {AuthService} from "../service/auth.service";

@Component({
  selector: 'app-side-menu',
  templateUrl: './side-menu.component.html',
  styleUrls: ['./side-menu.component.css']
})
export class SideMenuComponent implements OnInit {
  @Input()
  isMobile = false;
  @Output()
  routerLinkClickEmitter: EventEmitter<void> = new EventEmitter();

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

  routerLinkClicked() {
    if (this.isMobile) {
      this.routerLinkClickEmitter.emit();
    }
  }
}
