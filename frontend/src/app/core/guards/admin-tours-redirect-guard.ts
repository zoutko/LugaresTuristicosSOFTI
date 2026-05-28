// src/app/core/guards/admin-tours-redirect.guard.ts
import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthTokenService } from '../services/auth-token.service';
import { isAdminRole } from './admin.guard';

export const adminToursRedirectGuard: CanActivateFn = () => {
  const router = inject(Router);
  const authToken = inject(AuthTokenService);

  if (!authToken.hasToken()) return true;

  const role = localStorage.getItem('auth.role');
  if (!isAdminRole(role)) return true;

  return router.createUrlTree(['/admin/recorridos']);
};