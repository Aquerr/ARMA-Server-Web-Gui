import { TestBed } from "@angular/core/testing";

import { ServerModsService } from "./server-mods.service";

describe("ServerModsService", () => {
  let service: ServerModsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ServerModsService);
  });

  it("should be created", () => {
    expect(service).toBeTruthy();
  });
});
