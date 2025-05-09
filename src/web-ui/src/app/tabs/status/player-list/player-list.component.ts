import { Component } from "@angular/core";
import { ArmaServerPlayer } from "../../../model/arma-server-player.model";

@Component({
  selector: "app-player-list",
  templateUrl: "./player-list.component.html",
  styleUrls: ["./player-list.component.scss"],
  standalone: false
})
export class PlayerListComponent {
  playerList: ArmaServerPlayer[] = [];
}
