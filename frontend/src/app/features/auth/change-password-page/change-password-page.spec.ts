import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { ActivatedRoute } from '@angular/router';
import { of } from 'rxjs';

import { ChangePasswordPage } from './change-password-page';
import { AuthService } from '../../../core/services/auth.service';

class AuthServiceStub {
  login() {
    return of({
      userId: 1,
      token: 'temp-token',
      type: 'Bearer',
      email: 'user@test.com',
      role: 'USER',
    });
  }

  changePassword() {
    return of({ message: 'ok' });
  }
}

describe('ChangePasswordPage', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [ChangePasswordPage],
      providers: [
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              queryParamMap: {
                get: (key: string) => {
                  if (key === 'email') return 'user@test.com';
                  if (key === 'returnUrl') return '/lugares';
                  return null;
                },
              },
            },
          },
        },
        { provide: AuthService, useClass: AuthServiceStub },
      ],
    }).compileComponents();
  });

  it('navigates back to /auth preserving returnUrl after successful password change', async () => {
    const fixture = TestBed.createComponent(ChangePasswordPage);
    const component = fixture.componentInstance;

    const router = TestBed.inject(Router);
    spyOn(router, 'navigate').and.resolveTo(true);

    component.temporaryPassword = 'temp';
    component.newPassword = 'newpass';
    component.confirmPassword = 'newpass';

    await component.submit();

    expect(router.navigate).toHaveBeenCalledWith(['/auth'], { queryParams: { returnUrl: '/lugares' } });
  });

  it('cancel navigates back to /auth preserving returnUrl', async () => {
    const fixture = TestBed.createComponent(ChangePasswordPage);
    const component = fixture.componentInstance;

    const router = TestBed.inject(Router);
    spyOn(router, 'navigate').and.resolveTo(true);

    component.cancel();

    expect(router.navigate).toHaveBeenCalledWith(['/auth'], { queryParams: { returnUrl: '/lugares' } });
  });
});
