import { TestBed } from '@angular/core/testing';

import { ModInstallWebsocketService } from './mod-install-websocket.service';

describe('ModInstallWebsocketService', () => {
  let service: ModInstallWebsocketService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ModInstallWebsocketService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
