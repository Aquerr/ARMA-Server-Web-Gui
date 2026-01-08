import { ComponentFixture, TestBed } from "@angular/core/testing";

import { WorkshopComponent } from "./workshop.component";
import { WorkshopServiceMock } from "../../../../testing/mocks/workshop-service.mock";
import { WorkshopService } from "../../service/workshop.service";
import { EMPTY } from "rxjs";

describe("WorkshopComponent", () => {
  let component: WorkshopComponent;
  let fixture: ComponentFixture<WorkshopComponent>;

  const workshopServiceMock = new WorkshopServiceMock();

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WorkshopComponent],
      providers: [{
        provide: WorkshopService, useValue: workshopServiceMock
      }]
    }).compileComponents();

    workshopServiceMock.getInstalledWorkshopItems.mockReturnValue(EMPTY);

    fixture = TestBed.createComponent(WorkshopComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
