import { Injectable } from '@angular/core';
import {Observable, tap} from "rxjs";
import { HttpClient } from "@angular/common/http";
import {API_BASE_URL} from "../../environments/environment";
import {JwtTokenResponse} from "../model/jwt.model";
import {AswgAuthority} from "../model/authority.model";

@Injectable({
  providedIn: 'root'
})
export class AuthService  {

  private static readonly STORAGE_USERNAME_KEY = "username";
  private static readonly STORAGE_AUTH_TOKEN_KEY = "auth-token";
  private static readonly STORAGE_AUTHORITIES_KEY = "authorities";

  constructor(private readonly httpClient: HttpClient) {

  }

  authenticate(username: string, password: string): Observable<any> {
    return this.httpClient.post<JwtTokenResponse>(API_BASE_URL + '/auth', {username, password},
      {
        observe: "response"
      })
      .pipe(tap(userData => {
        sessionStorage.setItem(AuthService.STORAGE_USERNAME_KEY, username);
        sessionStorage.setItem(AuthService.STORAGE_AUTH_TOKEN_KEY, (userData.body?.jwt ? userData.body?.jwt : ''));
        sessionStorage.setItem(AuthService.STORAGE_AUTHORITIES_KEY, JSON.stringify(userData.body?.authorities || []));
        return userData;
      }));
  }

  isAuthenticated(): boolean {
    let user = this.getUsername();
    let token = this.getAuthToken();
    return user != null && token != null;
  }

  clearAuth(): void {
    sessionStorage.removeItem(AuthService.STORAGE_USERNAME_KEY);
    sessionStorage.removeItem(AuthService.STORAGE_AUTH_TOKEN_KEY);
  }

  logout(): void {
    this.httpClient.post(`${API_BASE_URL}/auth/logout`, null)
      .subscribe({
        complete: () => {
          this.clearAuth();
        }
      });
  }

  getUsername(): string | null {
    return sessionStorage.getItem(AuthService.STORAGE_USERNAME_KEY);
  }

  getAuthToken(): string | null {
    return sessionStorage.getItem(AuthService.STORAGE_AUTH_TOKEN_KEY);
  }

  getAuthorities(): AswgAuthority[] {
    const authoritiesString = sessionStorage.getItem(AuthService.STORAGE_AUTHORITIES_KEY);
    if (!authoritiesString)
      return [];

    return (JSON.parse(authoritiesString) as string[])
      .map(authority => authority as AswgAuthority);
  }
}
