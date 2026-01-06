import {
  Component,
  ComponentRef,
  Inject,
  OnInit,
  ViewChild,
  ViewContainerRef
} from "@angular/core";
import { MissionParameterComponent } from "./mission-parameter/mission-parameter.component";
import {
  MAT_DIALOG_DATA,
  MatDialogActions, MatDialogClose,
  MatDialogContent,
  MatDialogRef,
  MatDialogTitle
} from "@angular/material/dialog";
import { Mission, MissionDifficulty, MissionParam } from "../../../model/mission.model";
import { Subscription } from "rxjs";
import { MatFormField, MatInput, MatLabel } from "@angular/material/input";
import { FormsModule } from "@angular/forms";
import { MatOption, MatSelect } from "@angular/material/select";
import { MatButton } from "@angular/material/button";

@Component({
  selector: "app-mission-modify-dialog",
  templateUrl: "./mission-modify-dialog.component.html",
  imports: [
    MatDialogTitle,
    MatDialogContent,
    MatFormField,
    MatLabel,
    FormsModule,
    MatSelect,
    MatOption,
    MatButton,
    MatDialogActions,
    MatDialogClose,
    MatInput
  ],
  styleUrls: ["./mission-modify-dialog.component.scss"]
})
export class MissionModifyDialogComponent implements OnInit {
  @ViewChild("viewContainerRef", { read: ViewContainerRef, static: true })
  viewContainerRef!: ViewContainerRef;

  name: string = "";
  template: string = "";
  difficulty: MissionDifficulty = MissionDifficulty.REGULAR;
  parameters: MissionParam[] = [];

  constructor(
    @Inject(MAT_DIALOG_DATA) public mission: Mission,
    public dialogRef: MatDialogRef<MissionModifyDialogComponent>
  ) {}

  ngOnInit(): void {
    if (this.mission.parameters) {
      this.mission.parameters.forEach((parameter) => {
        this.createNewParameter(parameter.name, parameter.value);
      });
    }
    this.difficulty = this.mission.difficulty;
    this.name = this.mission.name;
    this.template = this.mission.template;
  }

  createNewParameter(name: string | null, value: string | null) {
    const missionParam: MissionParam = {
      name: name ?? "",
      value: value ?? ""
    };
    this.parameters.push(missionParam);

    const component = this.viewContainerRef.createComponent(MissionParameterComponent);
    component.instance.parameter = missionParam;
    const missionParamSubscription = component.instance.parameterDeleted.subscribe((parameter) => {
      this.removeParameter(parameter, component, missionParamSubscription);
    });
  }

  removeParameter(
    parameter: MissionParam,
    componentRef: ComponentRef<MissionParameterComponent>,
    subscription: Subscription
  ) {
    subscription.unsubscribe();
    componentRef.destroy();
    const index = this.parameters.indexOf(parameter);
    if (index != -1) {
      this.parameters.splice(index, 1);
    }
  }

  onMissionParamsSave() {
    this.mission.parameters = this.parameters;
    this.mission.difficulty = this.difficulty;
    this.mission.name = this.name;
    this.mission.template = this.template;
    this.dialogRef.close(this.mission);
  }
}
