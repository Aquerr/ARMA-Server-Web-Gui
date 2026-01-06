import { Injectable } from "@angular/core";
import { map, Observable, tap } from "rxjs";
import { HttpClient, HttpResponse } from "@angular/common/http";
import { API_BASE_URL } from "../../environments/environment";
import { JwtTokenResponse } from "../model/jwt.model";
import { AswgAuthority } from "../model/authority.model";

@Injectable({
  providedIn: "root"
})
export class AuthService {
  private static readonly STORAGE_USERNAME_KEY = "username";
  private static readonly STORAGE_AUTH_TOKEN_KEY = "auth-token";
  private static readonly STORAGE_AUTHORITIES_KEY = "authorities";

  constructor(private readonly httpClient: HttpClient) {}

  authenticate(username: string, password: string): Observable<HttpResponse<JwtTokenResponse>> {
    return this.httpClient
      .post<JwtTokenResponse>(
        API_BASE_URL + "/auth",
        { username, password },
        {
          observe: "response"
        }
      )
      .pipe(
        map((userData) => {
          sessionStorage.setItem(AuthService.STORAGE_USERNAME_KEY, username);
          sessionStorage.setItem(
            AuthService.STORAGE_AUTH_TOKEN_KEY,
            userData.body?.jwt ?? ""
          );
          sessionStorage.setItem(
            AuthService.STORAGE_AUTHORITIES_KEY,
            JSON.stringify(userData.body?.authorities ?? [])
          );
          return userData;
        })
      );
  }

  isAuthenticated(): boolean {
    const user = this.getUsername();
    const token = this.getAuthToken();
    return user != null && token != null;
  }

  clearAuth(): void {
    sessionStorage.removeItem(AuthService.STORAGE_USERNAME_KEY);
    sessionStorage.removeItem(AuthService.STORAGE_AUTH_TOKEN_KEY);
  }

  logout(): Observable<void> {
    return this.httpClient
      .post<void>(`${API_BASE_URL}/auth/logout`, null)
      .pipe(tap(() => this.clearAuth()));
  }

  getUsername(): string | null {
    return sessionStorage.getItem(AuthService.STORAGE_USERNAME_KEY);
  }

  getAuthToken(): string | null {
    return sessionStorage.getItem(AuthService.STORAGE_AUTH_TOKEN_KEY);
  }

  getAuthorities(): AswgAuthority[] {
    const authoritiesString = sessionStorage.getItem(AuthService.STORAGE_AUTHORITIES_KEY);
    if (!authoritiesString) return [];

    return (JSON.parse(authoritiesString) as string[]).map(
      (authority) => authority as AswgAuthority
    );
  }
}
