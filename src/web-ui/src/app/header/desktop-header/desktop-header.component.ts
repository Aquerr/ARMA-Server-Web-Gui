import { ChangeDetectionStrategy, Component, inject, input, output } from "@angular/core";
import { Router, RouterLink } from "@angular/router";
import { finalize, take, tap } from "rxjs";
import { MatIconButton } from "@angular/material/button";
import { MatIcon } from "@angular/material/icon";
import { MatTooltip } from "@angular/material/tooltip";
import { AuthService } from "@service/auth.service";
import { LoadingSpinnerMaskService } from "@service/loading-spinner-mask.service";

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
  private readonly loadingSpinnerMaskService = inject(LoadingSpinnerMaskService);
  private readonly router = inject(Router);

  protected readonly isAuthenticated = this.authService.isAuthenticated;
  protected readonly username = this.authService.username;

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
    this.changeThemeEmit.emit();
  }
}
