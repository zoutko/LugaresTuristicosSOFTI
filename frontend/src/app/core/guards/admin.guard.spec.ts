import { TestBed } from '@angular/core/testing';
import { provideRouter, Router, UrlTree } from '@angular/router';

import { adminGuard } from './admin.guard';
import { AuthTokenService } from '../services/auth-token.service';

function makeState(url: string): { url: string } {
  return { url };
}

describe('adminGuard', () => {
  beforeEach(() => {
    localStorage.clear();

    TestBed.configureTestingModule({
      providers: [provideRouter([]), AuthTokenService],
    });
  });

  it('redirects to /auth with returnUrl when not logged in', () => {
    const router = TestBed.inject(Router);

    const result = TestBed.runInInjectionContext(() => adminGuard({} as any, makeState('/admin/lugares') as any));

    expect(result instanceof UrlTree).toBe(true);
    expect(router.serializeUrl(result as UrlTree)).toBe('/auth?returnUrl=%2Fadmin%2Flugares');
  });

  it('redirects to / when logged in but not admin', () => {
    localStorage.setItem('auth.token', 't');
    localStorage.setItem('auth.role', 'USER');

    const router = TestBed.inject(Router);

    const result = TestBed.runInInjectionContext(() => adminGuard({} as any, makeState('/admin/lugares') as any));

    expect(result instanceof UrlTree).toBe(true);
    expect(router.serializeUrl(result as UrlTree)).toBe('/');
  });

  it('allows access when logged in as admin', () => {
    localStorage.setItem('auth.token', 't');
    localStorage.setItem('auth.role', 'ADMINISTRATOR');

    const result = TestBed.runInInjectionContext(() => adminGuard({} as any, makeState('/admin/lugares') as any));

    expect(result).toBe(true);
  });
});
