import { ComponentFixture, TestBed } from "@angular/core/testing";

import { AswgSpinnerComponent } from "./aswg-spinner.component";

describe("AswgSpinnerComponent", () => {
  let component: AswgSpinnerComponent;
  let fixture: ComponentFixture<AswgSpinnerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [AswgSpinnerComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(AswgSpinnerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
