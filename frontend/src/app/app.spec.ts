import { Component } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { Router, provideRouter } from '@angular/router';
import { App } from './app';

@Component({ selector: 'app-dummy', template: '', standalone: true })
class DummyComponent {}

describe('App', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [App, DummyComponent],
      providers: [
        provideRouter([
          { path: '', component: DummyComponent },
          { path: 'auth', component: DummyComponent },
          { path: 'profile', component: DummyComponent },
          { path: 'lugares', component: DummyComponent },
        ]),
      ],
    }).compileComponents();

    localStorage.clear();
  });

  it('should create the app', () => {
    const fixture = TestBed.createComponent(App);
    const app = fixture.componentInstance;
    expect(app).toBeTruthy();
  });

  it('should render the topbar brand', () => {
    const fixture = TestBed.createComponent(App);
    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;

    const brandTitle = compiled.querySelector('.brand-title')?.textContent ?? '';
    expect(brandTitle).toContain('Recorridos');
  });

  it('should show login option when logged out', () => {
    const fixture = TestBed.createComponent(App);
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    const userButton = compiled.querySelector('button.user') as HTMLButtonElement | null;
    expect(userButton).toBeTruthy();

    userButton!.click();
    fixture.detectChanges();

    const dropdown = compiled.querySelector('.user-dropdown');
    expect(dropdown).toBeTruthy();

    const dropdownText = (dropdown?.textContent ?? '').toLowerCase();
    expect(dropdownText).toContain('iniciar');
    expect(dropdownText).not.toContain('cerrar');
  });

  it('should show profile and logout options when logged in', () => {
    localStorage.setItem('auth.token', 'test-token');

    const fixture = TestBed.createComponent(App);
    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    const userButton = compiled.querySelector('button.user') as HTMLButtonElement;

    userButton.click();
    fixture.detectChanges();

    const dropdown = compiled.querySelector('.user-dropdown');
    expect(dropdown).toBeTruthy();

    const dropdownText = (dropdown?.textContent ?? '').toLowerCase();
    expect(dropdownText).toContain('perfil');
    expect(dropdownText).toContain('cerrar');
  });

  it('should show only logout option when logged in and on profile page', async () => {
    localStorage.setItem('auth.token', 'test-token');

    const fixture = TestBed.createComponent(App);
    const router = TestBed.inject(Router);
    await router.navigateByUrl('/profile');

    fixture.detectChanges();
    const compiled = fixture.nativeElement as HTMLElement;

    (compiled.querySelector('button.user') as HTMLButtonElement).click();
    fixture.detectChanges();

    const dropdown = compiled.querySelector('.user-dropdown');
    expect(dropdown).toBeTruthy();

    const dropdownText = (dropdown?.textContent ?? '').toLowerCase();
    expect(dropdownText).toContain('cerrar');
    expect(dropdownText).not.toContain('perfil');
  });

  it('should clear auth storage and navigate to auth on logout', async () => {
    localStorage.setItem('auth.token', 'test-token');
    localStorage.setItem('auth.email', 'test@example.com');
    localStorage.setItem('auth.role', 'USER');
    localStorage.setItem('auth.userId', '1');

    const fixture = TestBed.createComponent(App);
    const router = TestBed.inject(Router);
    spyOn(router, 'navigate').and.resolveTo(true);

    fixture.detectChanges();

    const compiled = fixture.nativeElement as HTMLElement;
    (compiled.querySelector('button.user') as HTMLButtonElement).click();
    fixture.detectChanges();

    const logoutButton = compiled.querySelector(
      '.user-dropdown button.user-dropdown-item'
    ) as HTMLButtonElement | null;
    expect(logoutButton).toBeTruthy();

    logoutButton!.click();
    fixture.detectChanges();

    expect(localStorage.getItem('auth.token')).toBeNull();
    expect(localStorage.getItem('auth.email')).toBeNull();
    expect(localStorage.getItem('auth.role')).toBeNull();
    expect(localStorage.getItem('auth.userId')).toBeNull();

    expect(router.navigate).toHaveBeenCalled();
  });
});
