import { TestBed } from '@angular/core/testing';

import { DataPurgeService } from './data-purge.service';

describe('DataPurgeService', () => {
  let service: DataPurgeService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DataPurgeService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
