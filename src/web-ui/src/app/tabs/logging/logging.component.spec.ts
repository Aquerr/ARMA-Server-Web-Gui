import { ComponentFixture, TestBed } from "@angular/core/testing";

import { LoggingComponent } from "./logging.component";
import { provideToastr } from "ngx-toastr";
import { ServerLoggingServiceMock } from "../../../../testing/mocks/server-logging-service.mock";
import { ServerLoggingService } from "../../service/server-logging.service";
import { EMPTY } from "rxjs";

describe("LoggingComponent", () => {
  let component: LoggingComponent;
  let fixture: ComponentFixture<LoggingComponent>;

  const serverLoggingServiceMock = new ServerLoggingServiceMock();

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoggingComponent],
      providers: [provideToastr(), {
        provide: ServerLoggingService, useValue: serverLoggingServiceMock
      }]
    }).compileComponents();

    serverLoggingServiceMock.getLoggingSectionData.mockReturnValue(EMPTY);

    fixture = TestBed.createComponent(LoggingComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
