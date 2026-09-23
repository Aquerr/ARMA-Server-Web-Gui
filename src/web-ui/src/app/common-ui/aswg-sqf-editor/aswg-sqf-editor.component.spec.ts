import { ComponentFixture, TestBed } from "@angular/core/testing";

import { AswgSqfEditorComponent } from "./aswg-sqf-editor.component";

describe("AswgSqfEditorComponent", () => {
  let component: AswgSqfEditorComponent;
  let fixture: ComponentFixture<AswgSqfEditorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AswgSqfEditorComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(AswgSqfEditorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
