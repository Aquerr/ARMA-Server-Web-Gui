import { ChangeDetectionStrategy, Component, inject, input, output } from "@angular/core";
import { MatIcon } from "@angular/material/icon";
import { RouterLink } from "@angular/router";
import { AuthService } from "@service/auth.service";
import { SideMenuComponent } from "@app/side-menu/side-menu.component";

@Component({
  selector: "app-mobile-header",
  templateUrl: "./mobile-header.component.html",
  imports: [
    MatIcon,
    SideMenuComponent,
    RouterLink
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  styleUrls: ["./mobile-header.component.scss"]
})
export class MobileHeaderComponent {
  public readonly darkMode = input<boolean>(true);
  public readonly changeThemeEmit = output<void>();

  protected sideMenuExpanded = false;

  private readonly authService = inject(AuthService);

  protected readonly isAuthenticated = this.authService.isAuthenticated;
  protected readonly username = this.authService.username;

  toggleSideMenu() {
    this.sideMenuExpanded = !this.sideMenuExpanded;
  }

  closeSideMenu() {
    this.sideMenuExpanded = false;
  }

  changeTheme() {
    this.changeThemeEmit.emit();
  }
}
