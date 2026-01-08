import { ComponentFixture, TestBed } from "@angular/core/testing";

import { OverwriteCommandlineParamsModalComponent } from "./overwrite-commandline-params-modal.component";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";

describe("OverwriteCommandlineParamsModalComponent", () => {
  let component: OverwriteCommandlineParamsModalComponent;
  let fixture: ComponentFixture<OverwriteCommandlineParamsModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [OverwriteCommandlineParamsModalComponent],
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

    fixture = TestBed.createComponent(OverwriteCommandlineParamsModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
