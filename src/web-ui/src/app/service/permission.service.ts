import {inject, Injectable} from '@angular/core';
import {CanActivateFn, Router} from "@angular/router";
import {AuthService} from "./auth.service";
import {AswgAuthority} from "../model/authority.model";
import {NotificationService} from "./notification.service";

@Injectable({
  providedIn: 'root'
})
export class PermissionService {

  authService: AuthService = inject(AuthService);
  notificationService: NotificationService = inject(NotificationService);
  router: Router = inject(Router);

  constructor() {
  }


  //TODO: Add observable methods that can be piped when succeed
  hasAllAuthorities(requiredAuthorities: AswgAuthority[], shouldNotify: boolean): boolean {

    if (!this.authService.isAuthenticated()) {
      this.router.navigate(['login']);
      return false;
    }

    const userAuthorities = this.authService.getAuthorities();
    let hasRequiredAuthorities = true;

    for (const requiredAuthority of requiredAuthorities) {
      if (!userAuthorities.includes(requiredAuthority)) {
        hasRequiredAuthorities = false;
        break;
      }
    }

    if (!hasRequiredAuthorities && shouldNotify) {
      this.notifyAboutNoAccess();
    }

    return hasRequiredAuthorities;
  }

  hasAnyAuthority(requiredAuthorities: AswgAuthority[], shouldNotify: boolean): boolean {

    if (!this.authService.isAuthenticated()) {
      this.router.navigate(['login']);
      return false;
    }

    const userAuthorities = this.authService.getAuthorities();
    let hasRequiredAuthority = false;
    for (const requiredAuthority of requiredAuthorities) {
      if (userAuthorities.includes(requiredAuthority)) {
        hasRequiredAuthority = true;
        break;
      }
    }

    if (!hasRequiredAuthority && shouldNotify) {
      this.notifyAboutNoAccess();
    }

    return hasRequiredAuthority;
  }

  isAuthenticated(): boolean {
    if (!this.authService.isAuthenticated()) {
      this.router.navigate(['login']);
      return false;
    }
    return true;
  }

  private notifyAboutNoAccess() {
    this.notificationService.warningNotification("You don't have access to do this!");
  }
}

export function hasAllAuthorities(requiredRoles: AswgAuthority[], shouldNotify: boolean = true): CanActivateFn {
  return (route, state) => {
    return inject(PermissionService).hasAllAuthorities(requiredRoles, shouldNotify);
  }
}

export function hasAnyAuthority(authorities: AswgAuthority[], shouldNotify: boolean = true): CanActivateFn {
  return (route, state) => {
    return inject(PermissionService).hasAnyAuthority(authorities, shouldNotify);
  }
}

export function isAuthenticated(): CanActivateFn {
  return (route, state) => {
    return inject(PermissionService).isAuthenticated();
  }
}
