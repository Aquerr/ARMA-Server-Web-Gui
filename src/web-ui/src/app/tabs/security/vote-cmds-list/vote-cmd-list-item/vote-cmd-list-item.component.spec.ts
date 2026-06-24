import { ComponentFixture, TestBed } from "@angular/core/testing";

import { VoteCmdListItemComponent } from "./vote-cmd-list-item.component";
import { inputBinding } from "@angular/core";
import { FormControl, FormGroup } from "@angular/forms";

describe("VoteCmdListItemComponent", () => {
  let component: VoteCmdListItemComponent;
  let fixture: ComponentFixture<VoteCmdListItemComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VoteCmdListItemComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(VoteCmdListItemComponent, {
      bindings: [
        inputBinding("voteCmdFormGroup", () => {
          return new FormGroup({
            command: new FormGroup({
              name: new FormControl("undefined"),
              allowedPreMission: new FormControl(false),
              allowedPostMission: new FormControl(false),
              votingThreshold: new FormControl(0),
              percentageSideVotingThreshold: new FormControl(0)
            }),
            editing: new FormControl(false)
          });
        })
      ]
    });
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
