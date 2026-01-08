import { TestBed } from "@angular/core/testing";

import { ServerNetworkService } from "./server-network.service";
import { provideHttpClientTesting } from "@angular/common/http/testing";

describe("ServerNetworkService", () => {
  let service: ServerNetworkService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(ServerNetworkService);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });
});
