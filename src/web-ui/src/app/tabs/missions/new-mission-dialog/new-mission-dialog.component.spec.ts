import { ComponentFixture, TestBed } from "@angular/core/testing";

import { NewMissionDialogComponent } from "./new-mission-dialog.component";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";

describe("NewMissionDialogComponent", () => {
  let component: NewMissionDialogComponent;
  let fixture: ComponentFixture<NewMissionDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NewMissionDialogComponent],
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
    }).compileComponents();

    fixture = TestBed.createComponent(NewMissionDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
