import { TestBed } from '@angular/core/testing';

import { ServerSecurityService } from './server-security.service';

describe('ServerSecurityService', () => {
  let service: ServerSecurityService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ServerSecurityService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
