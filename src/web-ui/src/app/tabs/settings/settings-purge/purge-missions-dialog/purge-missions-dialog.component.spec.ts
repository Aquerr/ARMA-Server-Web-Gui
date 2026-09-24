import { ComponentFixture, TestBed } from "@angular/core/testing";

import { PurgeMissionsDialogComponent } from "./purge-missions-dialog.component";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";

describe("PurgeMissionsDialogComponent", () => {
  let component: PurgeMissionsDialogComponent;
  let fixture: ComponentFixture<PurgeMissionsDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PurgeMissionsDialogComponent],
      providers: [
        {
          provide: MatDialogRef,
          useValue: {}
        },
        {
          provide: MAT_DIALOG_DATA,
          useValue: {}
        }
      ]
    })
      .compileComponents();

    fixture = TestBed.createComponent(PurgeMissionsDialogComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
