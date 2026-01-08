import { ComponentFixture, TestBed } from "@angular/core/testing";

import { ModDownloadQueueDialogComponent } from "./mod-download-queue-dialog.component";
import { WorkshopServiceMock } from "../../../../../testing/mocks/workshop-service.mock";
import { WorkshopService } from "../../../service/workshop.service";
import { EMPTY } from "rxjs";

describe("ModDownloadQueueDialogComponent", () => {
  let component: ModDownloadQueueDialogComponent;
  let fixture: ComponentFixture<ModDownloadQueueDialogComponent>;

  const workshopServiceMock = new WorkshopServiceMock();

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ModDownloadQueueDialogComponent],
      providers: [
        {
          provide: WorkshopService, useValue: workshopServiceMock
        }
      ]
    })
      .compileComponents();

    workshopServiceMock.getModDownloadQueue.mockReturnValue(EMPTY);

    fixture = TestBed.createComponent(ModDownloadQueueDialogComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
