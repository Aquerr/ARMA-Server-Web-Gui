import { Injectable } from '@angular/core';
import {HttpClient, HttpRequest} from "@angular/common/http";
import {API_BASE_URL} from "../../environments/environment";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class ServerMissionsService {

  constructor(private httpClient: HttpClient) { }

  uploadMission(formData: FormData): Observable<any> {

    const request = new HttpRequest('POST', API_BASE_URL + "/missions", formData, {
      reportProgress: true
    });

    console.log(request);

    return this.httpClient.request(request);
    // return this.httpClient.post(API_BASE_URL + "/missions", formData);
  }
}
