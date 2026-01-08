import { ComponentFixture, TestBed } from "@angular/core/testing";

import { SecurityComponent } from "./security.component";
import { provideToastr } from "ngx-toastr";
import { provideHttpClientTesting } from "@angular/common/http/testing";
import { ServerSecurityServiceMock } from "../../../../testing/mocks/server-security-service.mock";
import { ServerSecurityService } from "../../service/server-security.service";
import { EMPTY } from "rxjs";

describe("SecurityComponent", () => {
  let component: SecurityComponent;
  let fixture: ComponentFixture<SecurityComponent>;

  const serverSecurityServiceMock = new ServerSecurityServiceMock();

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SecurityComponent],
      providers: [provideToastr(), provideHttpClientTesting(), {
        provide: ServerSecurityService, useValue: serverSecurityServiceMock
      }]
    }).compileComponents();

    serverSecurityServiceMock.getServerSecurity.mockReturnValue(EMPTY);
    serverSecurityServiceMock.saveServerSecurity.mockReturnValue(EMPTY);

    fixture = TestBed.createComponent(SecurityComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
