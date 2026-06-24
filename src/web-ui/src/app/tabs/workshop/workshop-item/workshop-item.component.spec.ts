import { ComponentFixture, TestBed } from "@angular/core/testing";

import { WorkshopItemComponent } from "./workshop-item.component";
import { inputBinding } from "@angular/core";

describe("WorkshopItemComponent", () => {
  let component: WorkshopItemComponent;
  let fixture: ComponentFixture<WorkshopItemComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WorkshopItemComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(WorkshopItemComponent, {
      bindings: [
        inputBinding("workshopMod", () => {
          return {};
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
