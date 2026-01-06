import { ComponentFixture, TestBed } from "@angular/core/testing";

import { ModDownloadQueueDialogComponent } from "./mod-download-queue-dialog.component";

describe("ModDownloadQueueDialogComponent", () => {
  let component: ModDownloadQueueDialogComponent;
  let fixture: ComponentFixture<ModDownloadQueueDialogComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ModDownloadQueueDialogComponent]
    })
      .compileComponents();

    fixture = TestBed.createComponent(ModDownloadQueueDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
