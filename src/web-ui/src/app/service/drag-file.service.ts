import {BehaviorSubject, Observable} from "rxjs";
import {Injectable} from "@angular/core";

@Injectable({
  providedIn: 'root'
})
export class DragFileService {
  private isDragEnabled: BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  public isDragEnabled$: Observable<boolean> = this.isDragEnabled.asObservable();

  public setDragEnabled(isDragEnabled: boolean) {
    this.isDragEnabled.next(isDragEnabled);
  }
}
