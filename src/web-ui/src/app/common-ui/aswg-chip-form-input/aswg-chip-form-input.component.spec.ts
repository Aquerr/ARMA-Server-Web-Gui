import { ComponentFixture, TestBed } from "@angular/core/testing";

import { AswgChipFormInputComponent } from "./aswg-chip-form-input.component";
import { FormControl } from "@angular/forms";
import { inputBinding } from "@angular/core";

describe("AswgChipFormInputComponent", () => {
  let component: AswgChipFormInputComponent;
  let fixture: ComponentFixture<AswgChipFormInputComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AswgChipFormInputComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(AswgChipFormInputComponent, {
      bindings: [
        inputBinding("control", () => new FormControl()),
        inputBinding("labelText", () => "Test")
      ]
    });
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
