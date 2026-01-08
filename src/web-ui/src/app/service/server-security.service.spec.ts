import { TestBed } from "@angular/core/testing";

import { ServerSecurityService } from "./server-security.service";
import { provideHttpClientTesting } from "@angular/common/http/testing";

describe("ServerSecurityService", () => {
  let service: ServerSecurityService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(ServerSecurityService);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });
});
