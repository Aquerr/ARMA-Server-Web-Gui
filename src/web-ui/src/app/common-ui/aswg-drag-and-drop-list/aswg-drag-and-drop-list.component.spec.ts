import { ComponentFixture, TestBed } from "@angular/core/testing";

import { AswgDragAndDropListComponent } from "./aswg-drag-and-drop-list.component";
import { inputBinding } from "@angular/core";

describe("AswgDragAndDropListComponent", () => {
  let component: AswgDragAndDropListComponent<string>;
  let fixture: ComponentFixture<AswgDragAndDropListComponent<string>>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AswgDragAndDropListComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(AswgDragAndDropListComponent<string>, {
      bindings: [
        inputBinding("listId", () => "test-id"),
        inputBinding("moveAllButtonName", () => "move all"),
        inputBinding("moveAllButtonIcon", () => ""),
        inputBinding("sortEnabled", () => false),
        inputBinding("listHeader", () => "test-header"),
        inputBinding("listItemTemplate", () => null),
        inputBinding("sortingOptions", () => [])
      ]
    });
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
