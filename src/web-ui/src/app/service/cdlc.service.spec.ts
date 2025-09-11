import { TestBed } from '@angular/core/testing';

import { CdlcService } from './cdlc.service';

describe('CdlcService', () => {
  let service: CdlcService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CdlcService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
