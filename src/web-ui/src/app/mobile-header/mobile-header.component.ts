import {Component, EventEmitter, Input, Output} from '@angular/core';
import {AuthService} from "../service/auth.service";

@Component({
    selector: 'app-mobile-header',
    templateUrl: './mobile-header.component.html',
    styleUrls: ['./mobile-header.component.scss'],
    standalone: false
})
export class MobileHeaderComponent {
  @Input()
  darkMode: boolean = true;

  @Output()
  changeThemeEmit: EventEmitter<void> = new EventEmitter();

  username: string | null = null;
  sideMenuExpanded = false;

  constructor(private authService: AuthService) {
    if (this.isAuthenticated()) this.username = this.authService.getUsername();
  }

  toggleSideMenu() {
    this.sideMenuExpanded = !this.sideMenuExpanded;
  }

  closeSideMenu() {
    this.sideMenuExpanded = false;
  }

  isAuthenticated(): boolean {
    return this.authService.isAuthenticated();
  }

  changeTheme() {
    this.changeThemeEmit.emit();
  }

}
