import { ComponentFixture, TestBed } from "@angular/core/testing";

import { ModForceUpdateConfirmDialogComponent } from "./mod-force-update-confirm-dialog.component";

describe("ModForceUpdateConfirmDialogComponent", () => {
  let component: ModForceUpdateConfirmDialogComponent;
  let fixture: ComponentFixture<ModForceUpdateConfirmDialogComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [ModForceUpdateConfirmDialogComponent]
    });
    fixture = TestBed.createComponent(ModForceUpdateConfirmDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
