import { TestBed } from "@angular/core/testing";

import { ServerGeneralService } from "./server-general.service";
import { provideToastr } from "ngx-toastr";
import { provideHttpClientTesting } from "@angular/common/http/testing";

describe("ServerGeneralService", () => {
  let service: ServerGeneralService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideToastr(), provideHttpClientTesting()]
    });
    service = TestBed.inject(ServerGeneralService);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });
});
