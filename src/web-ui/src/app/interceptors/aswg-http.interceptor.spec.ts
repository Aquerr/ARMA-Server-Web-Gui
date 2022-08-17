import { TestBed } from '@angular/core/testing';

import { AswgHttpInterceptor } from './aswg-http.interceptor';

describe('AuthInterceptor', () => {
  beforeEach(() => TestBed.configureTestingModule({
    providers: [
      AswgHttpInterceptor
      ]
  }));

  it('should be created', () => {
    const interceptor: AswgHttpInterceptor = TestBed.inject(AswgHttpInterceptor);
    expect(interceptor).toBeTruthy();
  });
});
