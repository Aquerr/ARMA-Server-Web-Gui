import { ComponentFixture, TestBed } from "@angular/core/testing";

import { PasswordChangeDialogComponent } from "./password-change-dialog.component";

describe("PasswordChangeModalComponent", () => {
  let component: PasswordChangeDialogComponent;
  let fixture: ComponentFixture<PasswordChangeDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PasswordChangeDialogComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(PasswordChangeDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
