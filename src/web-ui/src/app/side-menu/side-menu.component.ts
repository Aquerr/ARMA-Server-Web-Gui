import { Component, EventEmitter, Input, Output } from "@angular/core";
import { WorkshopService } from "../service/workshop.service";
import { AuthService } from "../service/auth.service";
import { Router } from "@angular/router";
import { NotificationService } from "../service/notification.service";
import { map, Observable, of, switchMap, take, tap } from "rxjs";
import { LoadingSpinnerMaskService } from "../service/loading-spinner-mask.service";
import { fromPromise } from "rxjs/internal/observable/innerFrom";

@Component({
  selector: "app-side-menu",
  templateUrl: "./side-menu.component.html",
  styleUrls: ["./side-menu.component.scss"],
  standalone: false
})
export class SideMenuComponent {
  @Input()
  isMobile = false;

  @Input()
  darkMode: boolean = true;

  @Output()
  routerLinkClickEmitter = new EventEmitter<string>();

  @Output()
  changeThemeEmit = new EventEmitter<void>();

  isWorkshopActive: boolean = false;
  routePreCheck = new Map<string, (routerLink: string) => Observable<boolean>>();

  constructor(
    private router: Router,
    private authService: AuthService,
    private workshopService: WorkshopService,
    private notificationService: NotificationService,
    private maskService: LoadingSpinnerMaskService
  ) {
    if (this.authService.isAuthenticated()) {
      this.workshopService.canUseWorkshop().subscribe((response) => {
        this.isWorkshopActive = response.active;
      });
    }

    this.routePreCheck.set("/workshop", () => this.canUseWorkshopRoute());
  }

  changeTheme() {
    this.changeThemeEmit.emit();
  }

  isAuthenticated() {
    return this.authService.isAuthenticated();
  }

  logout() {
    this.authService
      .logout()
      .pipe(
        switchMap(() => fromPromise(this.router.navigateByUrl("/login"))),
        take(1)
      )
      .subscribe();
  }

  routerLinkClicked(routerLink: string) {
    this.maskService.show();
    let preCheck = this.routePreCheck.get(routerLink);

    preCheck ??= SideMenuComponent.canUseRouteDefault;

    preCheck(routerLink).subscribe({
      next: (canAccessLink) => {
        if (!canAccessLink) {
          this.maskService.hide();
          return;
        }

        this.maskService.hide();
        void this.router.navigate([routerLink]);
        this.routerLinkClickEmitter.emit(routerLink);
      }
    });
  }

  private static canUseRouteDefault(): Observable<boolean> {
    return of(true);
  }

  private canUseWorkshopRoute(): Observable<boolean> {
    return this.workshopService.canUseWorkshop().pipe(
      map((response) => response.active),
      tap({
        next: (value) => {
          if (!value) {
            this.notificationService.warningNotification(
              "Steam not installed on the server.",
              "Warning"
            );
          }
        }
      })
    );
  }
}
