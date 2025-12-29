import { TestBed } from '@angular/core/testing';

import { IconRegistrarService } from './icon-registrar.service';

describe('IconRegistrarService', () => {
  let service: IconRegistrarService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(IconRegistrarService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
