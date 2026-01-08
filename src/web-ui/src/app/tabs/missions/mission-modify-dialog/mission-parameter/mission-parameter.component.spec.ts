import { ComponentFixture, TestBed } from "@angular/core/testing";

import { MissionParameterComponent } from "./mission-parameter.component";
import { MissionParam } from "../../../../model/mission.model";

describe("MissionParameterComponent", () => {
  let component: MissionParameterComponent;
  let fixture: ComponentFixture<MissionParameterComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MissionParameterComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(MissionParameterComponent);
    component = fixture.componentInstance;
    component.parameter = {
      name: "test",
      value: "test-value"
    } as MissionParam;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
