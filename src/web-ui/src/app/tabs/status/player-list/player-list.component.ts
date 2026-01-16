import { ChangeDetectionStrategy, Component, input, InputSignal } from "@angular/core";
import { ArmaServerPlayer } from "../../../model/arma-server-player.model";
import { MatList, MatListItem } from "@angular/material/list";
import { MatIcon } from "@angular/material/icon";

@Component({
  selector: "app-player-list",
  templateUrl: "./player-list.component.html",
  imports: [
    MatList,
    MatListItem,
    MatIcon
  ],
  styleUrls: ["./player-list.component.scss"],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PlayerListComponent {
  playerList: InputSignal<ArmaServerPlayer[]> = input<ArmaServerPlayer[]>([]);
}
