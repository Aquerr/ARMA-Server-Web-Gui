import { Component, EventEmitter, Input, Output } from "@angular/core";
import { AuthService } from "../service/auth.service";
import { Router } from "@angular/router";
import { take, tap } from "rxjs";

@Component({
  selector: "app-desktop-header",
  templateUrl: "./desktop-header.component.html",
  styleUrls: ["./desktop-header.component.scss"],
  standalone: false
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
