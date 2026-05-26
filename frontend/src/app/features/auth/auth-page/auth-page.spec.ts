import { TestBed, fakeAsync, flushMicrotasks } from '@angular/core/testing';
import { Router, provideRouter } from '@angular/router';
import { provideHttpClient } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { of, throwError } from 'rxjs';

import { AuthPage } from './auth-page';
import { AuthService, LoginResponse } from '../../../core/services/auth.service';
import { UserService } from '../../../core/services/user.service';

class AuthServiceStub {
  loginResult: LoginResponse | null = null;

  login() {
    if (this.loginResult) return of(this.loginResult);
    return throwError(() => new Error('login failed'));
  }

  recoverPassword() {
    return of({ message: 'ok' });
  }
}

class UserServiceStub {
  createUser() {
    return of({});
  }
}

describe('AuthPage', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AuthPage],
      providers: [
        provideHttpClient(),
        provideRouter([]),
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              queryParamMap: {
                get: (key: string) => {
                  if (key === 'returnUrl') return '/lugares';
                  return null;
                },
              },
            },
          },
        },
        { provide: AuthService, useClass: AuthServiceStub },
        { provide: UserService, useClass: UserServiceStub },
      ],
    }).compileComponents();

    localStorage.clear();
  });

  it('redirects to returnUrl after successful login', fakeAsync(() => {
    const fixture = TestBed.createComponent(AuthPage);
    const component = fixture.componentInstance;

    const authApi = TestBed.inject(AuthService) as unknown as AuthServiceStub;
    authApi.loginResult = {
      userId: 1,
      token: 'token',
      type: 'Bearer',
      email: 'a@b.com',
      role: 'USER',
    };

    const router = TestBed.inject(Router);
    spyOn(router, 'navigateByUrl').and.resolveTo(true);

    component.loginEmail = 'a@b.com';
    component.loginPassword = 'pw';

    component.submitLogin();

    flushMicrotasks();

    expect(localStorage.getItem('auth.token')).toBe('token');
    expect(router.navigateByUrl).toHaveBeenCalledWith('/lugares');
  }));

  it('falls back to / when returnUrl is unsafe (external)', fakeAsync(() => {
    const fixture = TestBed.createComponent(AuthPage);
    const component = fixture.componentInstance;

    const authApi = TestBed.inject(AuthService) as unknown as AuthServiceStub;
    authApi.loginResult = {
      userId: 1,
      token: 'token',
      type: 'Bearer',
      email: 'a@b.com',
      role: 'USER',
    };

    const router = TestBed.inject(Router);
    spyOn(router, 'navigateByUrl').and.resolveTo(true);

    const route = TestBed.inject(ActivatedRoute) as any;
    route.snapshot.queryParamMap.get = (key: string) => (key === 'returnUrl' ? 'https://evil.com' : null);

    component.loginEmail = 'a@b.com';
    component.loginPassword = 'pw';

    component.submitLogin();
    flushMicrotasks();

    expect(router.navigateByUrl).toHaveBeenCalledWith('/');
  }));
});
