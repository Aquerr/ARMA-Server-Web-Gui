import { ComponentFixture, TestBed } from "@angular/core/testing";

import { ModDeleteConfirmDialogComponent } from "./mod-delete-confirm-dialog.component";

describe("ModDeleteConfirmDialogComponent", () => {
  let component: ModDeleteConfirmDialogComponent;
  let fixture: ComponentFixture<ModDeleteConfirmDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ModDeleteConfirmDialogComponent]
    }).compileComponents();

    fixture = TestBed.createComponent(ModDeleteConfirmDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
