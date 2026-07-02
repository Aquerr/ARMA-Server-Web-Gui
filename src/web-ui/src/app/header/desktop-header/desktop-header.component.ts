import { ChangeDetectionStrategy, Component, inject, input, output } from "@angular/core";
import { Router, RouterLink } from "@angular/router";
import { take, tap } from "rxjs";
import { MatIconButton } from "@angular/material/button";
import { MatIcon } from "@angular/material/icon";
import { MatTooltip } from "@angular/material/tooltip";
import { AuthService } from "@service/auth.service";

@Component({
  selector: "app-desktop-header",
  templateUrl: "./desktop-header.component.html",
  imports: [
    RouterLink,
    MatIconButton,
    MatIcon,
    MatTooltip
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  styleUrls: ["./desktop-header.component.scss"]
})
export class DesktopHeaderComponent {
  public readonly darkMode = input<boolean>(true);
  public readonly changeThemeEmit = output<void>();
  public readonly routerLinkClickEmitter = output<string>();

  private readonly authService = inject(AuthService);
  private readonly router = inject(Router);

  logout() {
    this.authService
      .logout()
      .pipe(
        tap(() => void this.router.navigateByUrl("/login")),
        take(1)
      )
      .subscribe();
  }

  isAuthenticated() {
    return this.authService.isAuthenticated();
  }

  getUsername() {
    return this.authService.getUsername();
  }

  changeTheme() {
    this.changeThemeEmit.emit();
  }
}
