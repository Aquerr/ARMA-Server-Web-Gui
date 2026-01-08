import { TestBed } from "@angular/core/testing";

import { NotificationService } from "./notification.service";
import { provideToastr } from "ngx-toastr";

describe("NotificationService", () => {
  let service: NotificationService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideToastr()]
    });
    service = TestBed.inject(NotificationService);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });
});
