import { ComponentFixture, TestBed } from "@angular/core/testing";

import { PurgeModsDialogComponent } from "./purge-mods-dialog.component";
import { MAT_DIALOG_DATA, MatDialogRef } from "@angular/material/dialog";

describe("PurgeModsDialogComponent", () => {
  let component: PurgeModsDialogComponent;
  let fixture: ComponentFixture<PurgeModsDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PurgeModsDialogComponent],
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

    fixture = TestBed.createComponent(PurgeModsDialogComponent);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
