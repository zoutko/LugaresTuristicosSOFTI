import { TestBed } from '@angular/core/testing';

import { AuthTokenService } from './auth-token.service';

describe('AuthTokenService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [AuthTokenService],
    });

    localStorage.clear();
  });

  it('hasToken returns false when no token', () => {
    const service = TestBed.inject(AuthTokenService);
    expect(service.hasToken()).toBe(false);
  });

  it('hasToken returns true when token exists', () => {
    localStorage.setItem('auth.token', 't');
    const service = TestBed.inject(AuthTokenService);
    expect(service.hasToken()).toBe(true);
  });

  it('getAuthHeaders returns Authorization header when token exists', () => {
    localStorage.setItem('auth.token', 'abc');
    const service = TestBed.inject(AuthTokenService);

    const headers = service.getAuthHeaders();
    expect(headers.get('Authorization')).toBe('Bearer abc');
  });
});
