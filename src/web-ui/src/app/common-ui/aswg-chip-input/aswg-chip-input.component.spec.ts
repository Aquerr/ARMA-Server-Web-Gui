import { ComponentFixture, TestBed } from "@angular/core/testing";

import { AswgChipInputComponent } from "./aswg-chip-input.component";

describe("AswgChipInputComponent", () => {
  let component: AswgChipInputComponent;
  let fixture: ComponentFixture<AswgChipInputComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AswgChipInputComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(AswgChipInputComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
