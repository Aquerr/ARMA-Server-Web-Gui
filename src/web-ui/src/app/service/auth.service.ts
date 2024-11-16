import { Injectable } from '@angular/core';
import { Router } from "@angular/router";
import {map, Observable} from "rxjs";
import {HttpClient} from "@angular/common/http";
import {API_BASE_URL} from "../../environments/environment";
import {JwtTokenResponse} from "../model/jwt.model";

@Injectable({
  providedIn: 'root'
})
export class AuthService  {

  constructor(private router: Router,
              private httpClient: HttpClient) {

  }

  authenticate(username: string, password: string): Observable<any> {
    return this.httpClient.post<JwtTokenResponse>(API_BASE_URL + '/auth', {username, password},
      {
        observe: "response"
      })
      .pipe(map(userData => {
        sessionStorage.setItem("username", username);
        sessionStorage.setItem("auth-token", (userData.body?.value ? userData.body?.value : ''));
        return userData;
      }));
  }

  isAuthenticated(): boolean {
    let user = sessionStorage.getItem("username");
    let token = sessionStorage.getItem("auth-token");
    return !(user == null) && !(token == null);
  }

  logout(): void {
    sessionStorage.removeItem("username");
    sessionStorage.removeItem("auth-token");
  }

  canActivate(): boolean {
    if (!this.isAuthenticated()) {
      this.router.navigate(['login']);
      return false;
    }
    return true;
  }

  getAuthToken() {
    return sessionStorage.getItem("auth-token");
  }
}
