import {Component, ElementRef, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {ServerLoggingService} from "../../../service/server-logging.service";
import {API_BASE_URL} from "../../../../environments/environment";
import FetchEventSource from "fetch-event-source";
import {AuthService} from "../../../service/auth.service";

@Component({
  selector: 'app-server-console',
  templateUrl: './server-console.component.html',
  styleUrls: ['./server-console.component.css']
})
export class ServerConsoleComponent implements OnInit, OnDestroy {

  @ViewChild('console') private console!: ElementRef;

  logs = "";

  eventSource;

  constructor(private serverLoggingService: ServerLoggingService,
              private authService: AuthService) {
    const headers = new Headers();
    headers.set("Authorization", "Bearer " + authService.getAuthToken() || "");
    this.eventSource = new FetchEventSource(`${API_BASE_URL}/logging/logs-sse`, {
      withCredentials: true,
      headers: headers
    });
  }


  ngOnInit(): void {
    this.pollServerLogs();
  }

  ngOnDestroy(): void {
    this.eventSource.close();
  }

  private pollServerLogs() {
    this.serverLoggingService.pollServerLogsSse(this.eventSource).subscribe(message => {
      console.log(message);
      this.logs += message + "\n";
      this.scrollConsoleToBottom();
    });
  }

  private scrollConsoleToBottom() {
    this.console.nativeElement.scrollTop = this.console.nativeElement.scrollHeight;
  }
}