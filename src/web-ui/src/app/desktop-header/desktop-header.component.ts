import {Component, EventEmitter, Input, Output} from '@angular/core';
import {AuthService} from "../service/auth.service";
import {Router} from "@angular/router";
import {MaskService} from "../service/mask.service";
import {Observable, of} from "rxjs";

@Component({
    selector: 'app-desktop-header',
    templateUrl: './desktop-header.component.html',
    styleUrls: ['./desktop-header.component.scss'],
    standalone: false
})
export class DesktopHeaderComponent {

  @Input()
  darkMode: boolean = true;

  @Output()
  changeThemeEmit: EventEmitter<void> = new EventEmitter();
  @Output()
  routerLinkClickEmitter: EventEmitter<string> = new EventEmitter();
  routePreCheck = new Map<string, (routerLink: string) => Observable<boolean>>();

  constructor(private authService: AuthService,
              private router: Router,
              private maskService: MaskService) {
  }

  logout() {
    this.routerLinkClicked("/logout");
    this.authService.logout();
    this.router.navigateByUrl("/login");
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

  private routerLinkClicked(routerLink: string) {
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

  private canUseRouteDefault(): Observable<boolean> {
    return of(true);
  }

}
