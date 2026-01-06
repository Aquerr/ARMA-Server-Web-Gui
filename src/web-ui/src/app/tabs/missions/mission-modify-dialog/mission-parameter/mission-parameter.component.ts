import { Component, EventEmitter, Input, Output } from "@angular/core";
import { MissionParam } from "../../../../model/mission.model";
import { MatFormField, MatInput, MatLabel } from "@angular/material/input";
import { MatIcon } from "@angular/material/icon";
import { MatIconButton } from "@angular/material/button";
import { FormsModule } from "@angular/forms";

@Component({
  selector: "app-mission-parameter",
  templateUrl: "./mission-parameter.component.html",
  imports: [
    MatFormField,
    MatLabel,
    MatInput,
    MatIcon,
    MatIconButton,
    FormsModule
  ],
  styleUrls: ["./mission-parameter.component.scss"]
})
export class MissionParameterComponent {
  @Output() parameterDeleted: EventEmitter<MissionParam> = new EventEmitter<MissionParam>();

  @Input()
  parameter!: MissionParam;

  deleteParameter() {
    this.parameterDeleted.emit(this.parameter);
  }
}
