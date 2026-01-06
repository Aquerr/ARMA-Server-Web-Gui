import { Component, EventEmitter, Input, Output } from "@angular/core";
import { AuthService } from "../service/auth.service";
import { Router, RouterLink } from "@angular/router";
import { take, tap } from "rxjs";
import { MatIconButton } from "@angular/material/button";
import { MatIcon } from "@angular/material/icon";
import { MatTooltip } from "@angular/material/tooltip";

@Component({
  selector: "app-desktop-header",
  templateUrl: "./desktop-header.component.html",
  imports: [
    RouterLink,
    MatIconButton,
    MatIcon,
    MatTooltip
  ],
  styleUrls: ["./desktop-header.component.scss"]
})
export class DesktopHeaderComponent {
  @Input()
  darkMode: boolean = true;

  @Output()
  changeThemeEmit = new EventEmitter<void>();

  @Output()
  routerLinkClickEmitter = new EventEmitter<string>();

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

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
