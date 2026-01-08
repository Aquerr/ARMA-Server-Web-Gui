import { ComponentFixture, TestBed } from "@angular/core/testing";

import { NetworkComponent } from "./network.component";
import { provideToastr } from "ngx-toastr";
import { provideHttpClientTesting } from "@angular/common/http/testing";
import { ServerNetworkService } from "../../service/server-network.service";
import {
  ServerNetworkServiceMock
} from "../../../../testing/mocks/server-network-service.mock";
import { EMPTY } from "rxjs";

describe("NetworkComponent", () => {
  let component: NetworkComponent;
  let fixture: ComponentFixture<NetworkComponent>;

  const serverNetworkServiceMock = new ServerNetworkServiceMock();

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [NetworkComponent],
      providers: [provideToastr(), provideHttpClientTesting(), {
        provide: ServerNetworkService, useValue: serverNetworkServiceMock
      }]
    }).compileComponents();

    serverNetworkServiceMock.getServerNetworkProperties.mockReturnValue(EMPTY);
    serverNetworkServiceMock.saveServerNetworkProperties.mockReturnValue(EMPTY);

    fixture = TestBed.createComponent(NetworkComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it("should create", () => {
    expect(component).toBeTruthy();
  });
});
