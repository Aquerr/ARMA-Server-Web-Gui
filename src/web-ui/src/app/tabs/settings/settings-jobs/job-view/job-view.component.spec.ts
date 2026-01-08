import { ComponentFixture, TestBed } from "@angular/core/testing";

import { JobViewComponent } from "./job-view.component";
import { provideToastr } from "ngx-toastr";
import { provideRouter } from "@angular/router";
import { provideHttpClientTesting } from "@angular/common/http/testing";
import { JobSetttingsServiceMock } from "../../../../../../testing/mocks/job-setttings-service.mock";
import { JobSettingsService } from "../../../../service/job-settings.service";
import { EMPTY } from "rxjs";

describe("JobViewComponent", () => {
  let component: JobViewComponent;
  let fixture: ComponentFixture<JobViewComponent>;

  const jobSettingsServiceMock = new JobSetttingsServiceMock();

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [JobViewComponent],
      providers: [provideToastr(), provideRouter([]), provideHttpClientTesting(),
        {
          provide: JobSettingsService, useValue: jobSettingsServiceMock
        }]
    })
      .compileComponents();

    jobSettingsServiceMock.getJobSettings.mockReturnValue(EMPTY);

    fixture = TestBed.createComponent(JobViewComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
