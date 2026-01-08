import { ComponentFixture, TestBed } from "@angular/core/testing";

import { StatusComponent } from "./status.component";
import { provideToastr } from "ngx-toastr";
import { ServerStatusServiceMock } from "../../../../testing/mocks/server-status-service.mock";
import { ServerStatusService } from "../../service/server-status.service";
import { EMPTY } from "rxjs";

describe("StatusComponent", () => {
  let component: StatusComponent;
  let fixture: ComponentFixture<StatusComponent>;

  const serverStatusServiceMock = new ServerStatusServiceMock();

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [StatusComponent],
      providers: [provideToastr(), {
        provide: ServerStatusService, useValue: serverStatusServiceMock
      }]
    }).compileComponents();

    serverStatusServiceMock.getStatus.mockReturnValue(EMPTY);

    fixture = TestBed.createComponent(StatusComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
