import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {API_BASE_URL} from '../../environments/environment';
import {WorkshopQueryRequest, WorkshopQueryResponse} from '../model/workshop.model';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class WorkshopService {

  private readonly API_QUERY_WORKSHOP = `${API_BASE_URL}/workshop/query`;

  constructor(private httpClient: HttpClient) { }

  queryWorkshop(workshopQueryRequest: WorkshopQueryRequest): Observable<WorkshopQueryResponse> {
    return this.httpClient.post<WorkshopQueryResponse>(this.API_QUERY_WORKSHOP, workshopQueryRequest);
  }
}
