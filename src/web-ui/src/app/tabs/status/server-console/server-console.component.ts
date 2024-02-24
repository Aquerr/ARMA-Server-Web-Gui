import {Component, ElementRef, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {ServerLoggingService} from "../../../service/server-logging.service";
import {API_BASE_URL} from "../../../../environments/environment";
import FetchEventSource from "fetch-event-source";
import {AuthService} from "../../../service/auth.service";
import {Observable} from "rxjs";

@Component({
  selector: 'app-server-console',
  templateUrl: './server-console.component.html',
  styleUrls: ['./server-console.component.css']
})
export class ServerConsoleComponent implements OnInit, OnDestroy {

  @ViewChild('console') private console!: ElementRef;

  logs = "";

  eventSource!: FetchEventSource;

  constructor(private serverLoggingService: ServerLoggingService,
              private authService: AuthService) {}


  ngOnInit(): void {
    this.initWithLatestLogs();
    this.awaitForLogs();
  }

  ngOnDestroy(): void {
    this.eventSource.close();
  }

  private awaitForLogs() {
    const headers = new Headers();
    headers.set("Authorization", "Bearer " + this.authService.getAuthToken() || "");
    this.eventSource = new FetchEventSource(`${API_BASE_URL}/logging/logs-sse`, {
      withCredentials: true,
      headers: headers
    });

    this.fetchEventSource(this.eventSource).subscribe({
      next: value => {
        this.logs += value + "\n";
        this.scrollConsoleToBottom();
      },
      error: err => {
        console.error(err);
      }
    });
  }

  fetchEventSource(eventSource: FetchEventSource): Observable<string> {
    return new Observable(observer => {
      eventSource.onmessage = event => {
        observer.next(event?.data);
      }
      eventSource.onerror = event => {
        observer.error(event?.message);
      }
    });
  }

  private scrollConsoleToBottom() {
    this.console.nativeElement.scrollTop = this.console.nativeElement.scrollHeight;
  }

  private initWithLatestLogs() {
    this.serverLoggingService.getLatestServerLogs().subscribe(response => {
      response.logs.forEach(log => this.logs += log + "\n");
      this.scrollConsoleToBottom();
    });
  }
}
