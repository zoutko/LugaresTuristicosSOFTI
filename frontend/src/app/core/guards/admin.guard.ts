import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { AuthTokenService } from '../services/auth-token.service';

const ROLE_STORAGE_KEY = 'auth.role';

export function isAdminRole(role: string | null): boolean {
  if (!role) return false;
  const normalized = role.trim().toUpperCase();

  if (normalized === 'ADMIN' || normalized === 'ADMINISTRATOR') return true;
  if (normalized === 'ROLE_ADMIN' || normalized === 'ROLE_ADMINISTRATOR') return true;

  return normalized.includes('ADMIN');
}

export const adminGuard: CanActivateFn = (_route, state) => {
  const router = inject(Router);
  const authToken = inject(AuthTokenService);

  if (!authToken.hasToken()) {
    return router.createUrlTree(['/auth'], { queryParams: { returnUrl: state.url } });
  }

  const role = localStorage.getItem(ROLE_STORAGE_KEY);
  if (!isAdminRole(role)) {
    return router.createUrlTree(['/']);
  }

  return true;
};
