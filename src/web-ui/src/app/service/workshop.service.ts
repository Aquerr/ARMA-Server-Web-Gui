import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {API_BASE_URL} from '../../environments/environment';
import {InstalledWorkshopItemsResponse, WorkshopQueryRequest, WorkshopQueryResponse} from '../model/workshop.model';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class WorkshopService {

  private readonly API_QUERY_WORKSHOP = `${API_BASE_URL}/workshop/query`;
  private readonly API_INSTALLED_ITEMS = `${API_BASE_URL}/workshop/installed-items`;

  constructor(private httpClient: HttpClient) { }

  queryWorkshop(workshopQueryRequest: WorkshopQueryRequest): Observable<WorkshopQueryResponse> {
    return this.httpClient.post<WorkshopQueryResponse>(this.API_QUERY_WORKSHOP, workshopQueryRequest);
  }

  getInstalledWorkshopItems(): Observable<InstalledWorkshopItemsResponse> {
    return this.httpClient.get<InstalledWorkshopItemsResponse>(this.API_INSTALLED_ITEMS);
  }
}
