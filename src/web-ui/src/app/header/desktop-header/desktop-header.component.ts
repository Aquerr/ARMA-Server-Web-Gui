import { ChangeDetectionStrategy, Component, inject, output } from "@angular/core";
import { Router, RouterLink } from "@angular/router";
import { finalize, take, tap } from "rxjs";
import { MatIconButton } from "@angular/material/button";
import { MatIcon } from "@angular/material/icon";
import { MatTooltip } from "@angular/material/tooltip";
import { AuthService } from "@service/auth.service";
import { LoadingSpinnerMaskService } from "@service/loading-spinner-mask.service";
import { ThemeService } from "@service/util/theme.service";

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
  public readonly routerLinkClickEmitter = output<string>();
  private readonly authService = inject(AuthService);

  private readonly loadingSpinnerMaskService = inject(LoadingSpinnerMaskService);
  private readonly router = inject(Router);
  private readonly themeService = inject(ThemeService);
  protected readonly isAuthenticated = this.authService.isAuthenticated;

  protected readonly username = this.authService.username;

  public readonly darkMode = this.themeService.darkMode;

  logout() {
    this.loadingSpinnerMaskService.show();
    this.authService
      .logout()
      .pipe(
        finalize(() => this.loadingSpinnerMaskService.hide()),
        tap(() => void this.router.navigateByUrl("/login")),
        take(1)
      )
      .subscribe();
  }

  changeTheme() {
    this.themeService.changeTheme();
  }
}
