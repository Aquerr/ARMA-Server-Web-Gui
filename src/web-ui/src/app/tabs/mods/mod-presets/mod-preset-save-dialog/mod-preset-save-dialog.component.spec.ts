import { ComponentFixture, TestBed } from "@angular/core/testing";

import { ModPresetSaveDialogComponent } from "./mod-preset-save-dialog.component";

describe("ModPresetSaveDialogComponent", () => {
  let component: ModPresetSaveDialogComponent;
  let fixture: ComponentFixture<ModPresetSaveDialogComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ModPresetSaveDialogComponent]
    });
    fixture = TestBed.createComponent(ModPresetSaveDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
