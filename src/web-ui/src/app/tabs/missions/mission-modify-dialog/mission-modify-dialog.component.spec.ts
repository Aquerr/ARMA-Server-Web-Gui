import { ComponentFixture, TestBed } from "@angular/core/testing";

import { MissionModifyDialogComponent } from "./mission-modify-dialog.component";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";

describe("MissionModifyDialogComponent", () => {
  let component: MissionModifyDialogComponent;
  let fixture: ComponentFixture<MissionModifyDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MissionModifyDialogComponent],
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

    fixture = TestBed.createComponent(MissionModifyDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
