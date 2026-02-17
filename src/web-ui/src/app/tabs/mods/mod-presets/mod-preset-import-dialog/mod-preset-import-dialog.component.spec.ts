import { ComponentFixture, TestBed } from "@angular/core/testing";

import { ModPresetImportDialogComponent } from "./mod-preset-import-dialog.component";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";

describe("ModPresetImportDialogComponent", () => {
  let component: ModPresetImportDialogComponent;
  let fixture: ComponentFixture<ModPresetImportDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ModPresetImportDialogComponent],
      providers: [
        {
          provide: MAT_DIALOG_DATA,
          useValue: {}
        },
        {
          provide: MatDialogRef,
          useValue: {}
        }
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(ModPresetImportDialogComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
