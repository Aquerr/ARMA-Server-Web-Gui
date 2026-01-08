import { ComponentFixture, TestBed } from "@angular/core/testing";

import { AswgChipFormInputComponent } from "./aswg-chip-form-input.component";
import { FormControl } from "@angular/forms";

describe("AswgChipFormInputComponent", () => {
  let component: AswgChipFormInputComponent;
  let fixture: ComponentFixture<AswgChipFormInputComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AswgChipFormInputComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(AswgChipFormInputComponent);
    component = fixture.componentInstance;
    component.control = new FormControl();
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
