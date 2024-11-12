import {Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {WorkshopService} from "../service/workshop.service";
import {AuthService} from "../service/auth.service";
import {Router} from "@angular/router";
import {NotificationService} from "../service/notification.service";
import {map, Observable, of, tap} from "rxjs";
import {MaskService} from "../service/mask.service";

@Component({
  selector: 'app-side-menu',
  templateUrl: './side-menu.component.html',
  styleUrls: ['./side-menu.component.scss']
})
export class SideMenuComponent implements OnInit {
  @Input()
  isMobile = false;
  @Input()
  darkMode: boolean = true;
  @Output()
  routerLinkClickEmitter: EventEmitter<string> = new EventEmitter();
  @Output()
  changeThemeEmit: EventEmitter<void> = new EventEmitter();

  isWorkshopActive: boolean = false;

  routePreCheck = new Map<string, (routerLink: string) => Observable<boolean>>();

  constructor(private router: Router,
              private authService: AuthService,
              private workshopService: WorkshopService,
              private notificationService: NotificationService,
              private maskService: MaskService) {

    if (this.authService.isAuthenticated()) {
      this.workshopService.canUseWorkshop().subscribe(response => {
        this.isWorkshopActive = response.active;
      })
    }

    this.routePreCheck.set("/workshop", () => this.canUseWorkshopRoute());
  }

  ngOnInit(): void {
  }

  isAuthenticated() {
    return this.authService.isAuthenticated();
  }

  routerLinkClicked(routerLink: string) {
    this.maskService.show();
    let preCheck = this.routePreCheck.get(routerLink);

    if (preCheck === undefined) {
      preCheck = this.canUseRouteDefault;
    }

    preCheck(routerLink).subscribe({
      next: canAccessLink => {
        if (!canAccessLink) {
          this.maskService.hide();
          return
        }

        this.maskService.hide();
        this.router.navigate([routerLink]);
        this.routerLinkClickEmitter.emit(routerLink);
      }
    });
  }

  changeTheme() {
    this.changeThemeEmit.emit();
  }

  logout() {
    this.routerLinkClicked("/logout");
    this.authService.logout();
    this.router.navigateByUrl("/login");
  }

  private canUseRouteDefault(): Observable<boolean> {
    return of(true);
  }

  private canUseWorkshopRoute(): Observable<boolean> {
    return this.workshopService.canUseWorkshop().pipe(
      map(response => response.active),
      tap({
        next: value => {
          if (!value) {
            this.notificationService.warningNotification("Steam not installed on the server.", "Warning");
          }
        }
      }));
  }
}
