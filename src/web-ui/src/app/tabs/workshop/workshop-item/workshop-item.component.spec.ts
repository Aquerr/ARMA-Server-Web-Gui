import { ComponentFixture, TestBed } from "@angular/core/testing";

import { WorkshopItemComponent } from "./workshop-item.component";

describe("WorkshopItemComponent", () => {
  let component: WorkshopItemComponent;
  let fixture: ComponentFixture<WorkshopItemComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [WorkshopItemComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(WorkshopItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
