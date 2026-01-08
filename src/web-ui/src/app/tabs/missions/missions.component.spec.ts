import { ComponentFixture, TestBed } from "@angular/core/testing";

import { MissionsComponent } from "./missions.component";
import { provideToastr } from "ngx-toastr";
import { ServerMissionsServiceMock } from "../../../../testing/mocks/server-missions-service.mock";
import { ServerMissionsService } from "../../service/server-missions.service";
import { EMPTY } from "rxjs";

describe("MissionsComponent", () => {
  let component: MissionsComponent;
  let fixture: ComponentFixture<MissionsComponent>;

  const serverMissionsServiceMock = new ServerMissionsServiceMock();

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MissionsComponent],
      providers: [
        provideToastr(),
        {
          provide: ServerMissionsService, useValue: serverMissionsServiceMock
        }
      ]
    }).compileComponents();

    serverMissionsServiceMock.getInstalledMissions.mockReturnValue(EMPTY);

    fixture = TestBed.createComponent(MissionsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
