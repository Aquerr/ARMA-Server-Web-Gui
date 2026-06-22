import { ComponentFixture, TestBed } from "@angular/core/testing";

import { VoteCmdListItemComponent } from "./vote-cmd-list-item.component";
import { VoteCmd } from "@model/vote-cmd.model";

describe("VoteCmdListItemComponent", () => {
  let component: VoteCmdListItemComponent;
  let fixture: ComponentFixture<VoteCmdListItemComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VoteCmdListItemComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(VoteCmdListItemComponent);
    component = fixture.componentInstance;
    component.item = {
      command: {} as VoteCmd,
      editing: false
    };
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
