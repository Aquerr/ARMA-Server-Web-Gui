import { ComponentFixture, TestBed } from "@angular/core/testing";

import { GeneralComponent } from "./general.component";
import { provideToastr } from "ngx-toastr";
import { ServerGeneralService } from "../../service/server-general.service";
import { EMPTY } from "rxjs";
import { ServerGeneralServiceMock } from "../../../../testing/mocks/server-general-service.mock";

describe("GeneralComponent", () => {
  let component: GeneralComponent;
  let fixture: ComponentFixture<GeneralComponent>;

  const serverGeneralServiceMock = new ServerGeneralServiceMock();

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [GeneralComponent],
      providers: [
        provideToastr(),
        {
          provide: ServerGeneralService, useValue: serverGeneralServiceMock
        }
      ]
    }).compileComponents();

    serverGeneralServiceMock.getGeneralProperties.mockReturnValue(EMPTY);
    serverGeneralServiceMock.saveGeneralProperties.mockReturnValue(EMPTY);

    fixture = TestBed.createComponent(GeneralComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
