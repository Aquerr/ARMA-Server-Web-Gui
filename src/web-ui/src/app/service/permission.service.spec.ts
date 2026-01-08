import { TestBed } from "@angular/core/testing";

import { PermissionService } from "./permission.service";
import { provideToastr } from "ngx-toastr";

describe("PermissionService", () => {
  let service: PermissionService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideToastr()]
    });
    service = TestBed.inject(PermissionService);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });
});
