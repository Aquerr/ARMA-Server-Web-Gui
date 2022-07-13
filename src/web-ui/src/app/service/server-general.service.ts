import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {API_BASE_URL} from "../../environments/environment";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class ServerGeneralService {

  constructor(private httpClient: HttpClient) { }

  home(): Observable<Object> {
    return this.httpClient.get(API_BASE_URL + "/");
  }
}
