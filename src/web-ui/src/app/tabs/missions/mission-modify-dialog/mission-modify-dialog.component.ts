import {Component, ComponentRef, OnInit, ViewChild, ViewContainerRef} from '@angular/core';
import {MissionParameterComponent} from "./mission-parameter/mission-parameter.component";

@Component({
  selector: 'app-mission-modify-dialog',
  templateUrl: './mission-modify-dialog.component.html',
  styleUrls: ['./mission-modify-dialog.component.css']
})
export class MissionModifyDialogComponent implements OnInit {

  @ViewChild("viewContainerRef", {read: ViewContainerRef}) viewContainerRef!: ViewContainerRef;

  parameters: ComponentRef<MissionParameterComponent>[] = [];

  constructor() { }

  ngOnInit(): void {
  }

  createNewParameter() {
    const component = this.viewContainerRef.createComponent(MissionParameterComponent);
    this.parameters.push(component);
  }

  removeParameter() {
    // const index = this.viewContainerRef.indexOf(this.ref.hostView);
    // if (index != -1) {
    //   this.viewContainerRef.remove(index);
    // }
  }
}
