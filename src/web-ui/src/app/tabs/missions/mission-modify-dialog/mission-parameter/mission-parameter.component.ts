import { Component, EventEmitter, Input, Output } from "@angular/core";
import { MissionParam } from "../../../../model/mission.model";

@Component({
  selector: "app-mission-parameter",
  templateUrl: "./mission-parameter.component.html",
  styleUrls: ["./mission-parameter.component.scss"],
  standalone: false
})
export class MissionParameterComponent {
  @Output() parameterDeleted: EventEmitter<MissionParam> = new EventEmitter<MissionParam>();

  @Input()
  parameter!: MissionParam;

  deleteParameter() {
    this.parameterDeleted.emit(this.parameter);
  }
}
