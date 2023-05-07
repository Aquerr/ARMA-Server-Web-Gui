import { TestBed } from '@angular/core/testing';

import { MissionUploadService } from './mission-upload.service';

describe('MissionUploadService', () => {
  let service: MissionUploadService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(MissionUploadService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
