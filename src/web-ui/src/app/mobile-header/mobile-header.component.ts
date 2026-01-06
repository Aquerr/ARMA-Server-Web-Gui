import { Component, EventEmitter, Input, Output } from "@angular/core";
import { AuthService } from "../service/auth.service";
import { MatIcon } from "@angular/material/icon";
import { SideMenuComponent } from "../side-menu/side-menu.component";
import { RouterLink } from "@angular/router";

@Component({
  selector: "app-mobile-header",
  templateUrl: "./mobile-header.component.html",
  imports: [
    MatIcon,
    SideMenuComponent,
    RouterLink
  ],
  styleUrls: ["./mobile-header.component.scss"]
})
export class MobileHeaderComponent {
  @Input()
  darkMode: boolean = true;

  @Output()
  changeThemeEmit = new EventEmitter<void>();

  sideMenuExpanded = false;

  constructor(private authService: AuthService) {}

  toggleSideMenu() {
    this.sideMenuExpanded = !this.sideMenuExpanded;
  }

  closeSideMenu() {
    this.sideMenuExpanded = false;
  }

  isAuthenticated(): boolean {
    return this.authService.isAuthenticated();
  }

  getUsername() {
    return this.authService.getUsername();
  }

  changeTheme() {
    this.changeThemeEmit.emit();
  }
}
