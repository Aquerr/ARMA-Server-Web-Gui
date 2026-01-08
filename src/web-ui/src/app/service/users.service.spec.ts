import { TestBed } from "@angular/core/testing";

import { UsersService } from "./users.service";
import { provideHttpClientTesting } from "@angular/common/http/testing";

describe("UsersService", () => {
  let service: UsersService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(UsersService);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });
});
