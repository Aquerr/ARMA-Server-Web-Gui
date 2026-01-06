import { EventEmitter, Injectable, Output } from "@angular/core";
import { API_WS_BASE_URL } from "../../../../environments/environment";
import { WorkShopModInstallStatus } from "../../../model/workshop.model";
import { API_WSS_BASE_URL } from "../../../../environments/environment.prod";

@Injectable({
  providedIn: "root"
})
export class ModInstallWebsocketService {
  websocket!: WebSocket;
  isConnected: boolean = false;

  @Output() workShopModInstallStatus: EventEmitter<WorkShopModInstallStatus>
    = new EventEmitter<WorkShopModInstallStatus>();

  connect() {
    if (this.websocket) {
      return;
    }

    const wsUrl = this.isHttps() ? API_WSS_BASE_URL : API_WS_BASE_URL;
    this.websocket = new WebSocket(wsUrl + "/workshop-mod-install-progress");
    this.websocket.onopen = () => {
      this.isConnected = true;
    };

    this.websocket.onmessage = (event) => {
      console.log(event);
      const status = event.data as WorkShopModInstallStatus;
      this.workShopModInstallStatus.emit(status);
    };

    this.websocket.onerror = (event) => {
      console.error(event);
    };

    this.websocket.onclose = (event) => {
      console.log(event);
      this.isConnected = false;
    };

    // const ws = new SockJS(this.webSocketEndPoint);
    // this.stompClient = Stomp.over(ws);
    // this.stompClient.connect({}, () => {
    //   console.log("Connection started!");
    //   this.listenForModMessages();
    // });

    // const rxStomp = new ModInstallWebsocketService();
    //
    //
    //
    //
    // const ws = new SockJS(this.webSocketEndPoint);
    // this.stompClient = RxStomp;
  }

  disconnect() {
    if (this.websocket) {
      this.websocket.close();
      this.isConnected = false;
    }
  }
  //
  // private listenForModMessages() {
  //   this.stompClient.subscribe(this.topic, (message) => {
  //     console.log("Message Received: " + message);
  //   });
  // }

  private isHttps(): boolean {
    return window.location.protocol === "https:";
  }
}
