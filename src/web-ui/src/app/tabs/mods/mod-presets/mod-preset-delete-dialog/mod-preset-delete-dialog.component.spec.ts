import { ComponentFixture, TestBed } from "@angular/core/testing";

import { ModPresetDeleteDialogComponent } from "./mod-preset-delete-dialog.component";

describe("ModPresetDeleteDialogComponent", () => {
  let component: ModPresetDeleteDialogComponent;
  let fixture: ComponentFixture<ModPresetDeleteDialogComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ModPresetDeleteDialogComponent]
    });
    fixture = TestBed.createComponent(ModPresetDeleteDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
