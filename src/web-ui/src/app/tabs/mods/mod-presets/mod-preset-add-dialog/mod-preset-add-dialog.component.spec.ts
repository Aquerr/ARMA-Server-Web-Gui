import { ComponentFixture, TestBed } from "@angular/core/testing";

import { ModPresetAddDialogComponent } from "./mod-preset-add-dialog.component";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";

describe("ModPresetAddDialogComponent", () => {
  let component: ModPresetAddDialogComponent;
  let fixture: ComponentFixture<ModPresetAddDialogComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [ModPresetAddDialogComponent],
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
    });
    fixture = TestBed.createComponent(ModPresetAddDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
