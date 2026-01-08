import { ComponentFixture, TestBed } from "@angular/core/testing";

import { CdlcComponent } from "./cdlc.component";
import { provideToastr } from "ngx-toastr";
import { CdlcServiceMock } from "../../../../testing/mocks/cdlc-service.mock";
import { CdlcService } from "../../service/cdlc.service";
import { EMPTY } from "rxjs";

describe("CdlcComponent", () => {
  let component: CdlcComponent;
  let fixture: ComponentFixture<CdlcComponent>;

  const cdlcServiceMock = new CdlcServiceMock();

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CdlcComponent],
      providers: [provideToastr(), {
        provide: CdlcService, useValue: cdlcServiceMock
      }]
    })
      .compileComponents();

    cdlcServiceMock.getAllCdlcs.mockReturnValue(EMPTY);

    fixture = TestBed.createComponent(CdlcComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
