import { TestBed } from '@angular/core/testing';

import { WorkshopService } from './workshop.service';

describe('WorkshopService', () => {
  let service: WorkshopService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(WorkshopService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
