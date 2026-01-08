import { ComponentFixture, TestBed } from "@angular/core/testing";

import { VoteCmdListItemComponent } from "./vote-cmd-list-item.component";
import { CommandListItem } from "./vote-cmd-list-item.model";
import { VoteCmd } from "../../../../model/vote-cmd.model";

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
    } as CommandListItem;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
