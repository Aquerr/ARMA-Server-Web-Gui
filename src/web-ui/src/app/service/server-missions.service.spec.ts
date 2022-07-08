import { TestBed } from '@angular/core/testing';

import { ServerMissionsService } from './server-missions.service';

describe('ServerMissionsService', () => {
  let service: ServerMissionsService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ServerMissionsService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
