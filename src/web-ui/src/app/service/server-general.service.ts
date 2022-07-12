import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {API_BASE_URL} from "../../environments/environment";

@Injectable({
  providedIn: 'root'
})
export class ServerGeneralService {

  constructor(private httpClient: HttpClient) { }

  home(): void {
    this.httpClient.get(API_BASE_URL + "/").subscribe(response => {
      console.log(response);
    });
  }
}
