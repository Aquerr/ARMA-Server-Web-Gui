import { ComponentFixture, TestBed } from "@angular/core/testing";

import { CommonConfirmDialogComponent } from "./common-confirm-dialog.component";
import { importProvidersFrom } from "@angular/core";
import { MAT_DIALOG_DATA, MatDialogModule } from "@angular/material/dialog";

describe("CommonConfirmDialogComponent", () => {
  let component: CommonConfirmDialogComponent;
  let fixture: ComponentFixture<CommonConfirmDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CommonConfirmDialogComponent],
      providers: [
        {
          provide: MAT_DIALOG_DATA,
          useValue: {}
        }]
    }).compileComponents();

    fixture = TestBed.createComponent(CommonConfirmDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
