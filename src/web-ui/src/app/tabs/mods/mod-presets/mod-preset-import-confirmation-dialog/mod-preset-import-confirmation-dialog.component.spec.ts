import { ComponentFixture, TestBed } from "@angular/core/testing";

import { ModPresetImportConfirmationDialogComponent } from "./mod-preset-import-confirmation-dialog.component";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";

describe("ModPresetImportDialogComponent", () => {
  let component: ModPresetImportConfirmationDialogComponent;
  let fixture: ComponentFixture<ModPresetImportConfirmationDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ModPresetImportConfirmationDialogComponent],
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

    fixture = TestBed.createComponent(ModPresetImportConfirmationDialogComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
