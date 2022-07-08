import { TestBed } from '@angular/core/testing';

import { ServerNetworkService } from './server-network.service';

describe('ServerNetworkService', () => {
  let service: ServerNetworkService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ServerNetworkService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
