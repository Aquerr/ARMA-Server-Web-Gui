import { ComponentFixture, TestBed } from "@angular/core/testing";

import { WorkshopItemComponent } from "./workshop-item.component";
import { WorkshopMod } from "../../../model/workshop.model";

describe("WorkshopItemComponent", () => {
  let component: WorkshopItemComponent;
  let fixture: ComponentFixture<WorkshopItemComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WorkshopItemComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(WorkshopItemComponent);
    component = fixture.componentInstance;
    component.workshopMod = {} as WorkshopMod;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
