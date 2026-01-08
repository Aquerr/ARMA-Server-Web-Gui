import { TestBed } from "@angular/core/testing";

import { AswgHttpInterceptor } from "./aswg-http.interceptor";
import { provideToastr } from "ngx-toastr";

describe("AswgHttpInterceptor", () => {
  beforeEach(() =>
    TestBed.configureTestingModule({
      providers: [AswgHttpInterceptor, provideToastr()]
    })
  );

  it("should be created", () => {
    const interceptor: AswgHttpInterceptor = TestBed.inject(AswgHttpInterceptor);
    expect(interceptor).toBeTruthy();
  });
});
