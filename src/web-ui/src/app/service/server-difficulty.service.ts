import { Injectable } from "@angular/core";
import { forkJoin, Observable } from "rxjs";
import { DifficultyProfile } from "../model/difficulty-profile.model";
import { HttpClient } from "@angular/common/http";
import { API_BASE_URL } from "../../environments/environment";

@Injectable({
  providedIn: "root"
})
export class ServerDifficultyService {
  constructor(private httpClient: HttpClient) {}

  getDifficulties(): Observable<DifficultyProfile[]> {
    return this.httpClient.get<DifficultyProfile[]>(API_BASE_URL + "/difficulties");
  }

  createDifficulty(difficultyProfile: DifficultyProfile): Observable<object> {
    return this.httpClient.post(API_BASE_URL + "/difficulties", difficultyProfile);
  }

  updateDifficulty(id: number, difficultyProfile: DifficultyProfile): Observable<object> {
    return this.httpClient.put(API_BASE_URL + `/difficulties/${id}`, difficultyProfile);
  }

  deleteDifficulty(idOrName: number | string): Observable<object> {
    if (typeof idOrName == "number") {
      return this.httpClient.delete(API_BASE_URL + `/difficulties/${idOrName}`);
    } else {
      return this.httpClient.delete(API_BASE_URL + `/difficulties?name=${idOrName}`);
    }
  }

  saveDifficulties(difficultyProfiles: DifficultyProfile[]): Observable<object> {
    return forkJoin(
      difficultyProfiles.map((profile) => {
        if (profile.id === 0 || profile.id === undefined) {
          return this.createDifficulty(profile);
        } else {
          return this.updateDifficulty(profile.id, profile);
        }
      })
    );
  }
}
