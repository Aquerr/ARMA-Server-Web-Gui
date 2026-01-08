import { ComponentFixture, TestBed } from "@angular/core/testing";

import { SettingsJobsComponent } from "./settings-jobs.component";
import { JobSetttingsServiceMock } from "../../../../../testing/mocks/job-setttings-service.mock";
import { JobSettingsService } from "../../../service/job-settings.service";
import { EMPTY } from "rxjs";

describe("SettingsJobsComponent", () => {
  let component: SettingsJobsComponent;
  let fixture: ComponentFixture<SettingsJobsComponent>;

  const jobSettingsServiceMock = new JobSetttingsServiceMock();

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SettingsJobsComponent],
      providers: [
        {
          provide: JobSettingsService, useValue: jobSettingsServiceMock
        }
      ]
    })
      .compileComponents();

    jobSettingsServiceMock.getAllJobsNames.mockReturnValue(EMPTY);

    fixture = TestBed.createComponent(SettingsJobsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
