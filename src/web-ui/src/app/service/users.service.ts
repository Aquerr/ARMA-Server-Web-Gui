import {inject, Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {API_BASE_URL} from "../../environments/environment";
import {AswgAuthority} from "../model/authority.model";

@Injectable({
  providedIn: 'root'
})
export class UsersService {

  private readonly httpClient: HttpClient = inject(HttpClient);

  constructor() { }

  getUsers(): Observable<AswgUser[]> {
    return this.httpClient.get<AswgUser[]>(`${API_BASE_URL}/users`);
  }

  addNewUser(user: AswgUser) {
    return this.httpClient.post(`${API_BASE_URL}/users`, user);
  }

  updateUser(user: AswgUser) {
    return this.httpClient.put(`${API_BASE_URL}/users/${user.id}`, user);
  }

  deleteUser(id: number) {
    return this.httpClient.delete(`${API_BASE_URL}/users/${id}`);
  }
}

export interface AswgUser {
  id: number | null;
  username: string;
  password: string;
  authorities: AswgAuthority[];
  locked: boolean;
}
