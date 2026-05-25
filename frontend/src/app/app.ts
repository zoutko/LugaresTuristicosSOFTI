import { NgIf } from '@angular/common';
import { Component, HostListener } from '@angular/core';
import { Router, RouterOutlet, RouterLinkWithHref } from '@angular/router';

import { AuthTokenService } from './core/services/auth-token.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLinkWithHref, NgIf],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  userMenuOpen = false;

  constructor(
    private readonly router: Router,
    private readonly authToken: AuthTokenService,
  ) {}

  isAdministrator(): boolean {
    return localStorage.getItem('auth.role') === 'ADMINISTRATOR';
  }

  isLoggedIn(): boolean {
    return this.authToken.hasToken();
  }

  getReturnUrl(): string {
    return this.router.url || '/';
  }

  isOnProfilePage(): boolean {
    return this.router.url.startsWith('/profile');
  }

  toggleUserMenu(event: MouseEvent): void {
    event.stopPropagation();
    this.userMenuOpen = !this.userMenuOpen;
  }

  closeUserMenu(): void {
    this.userMenuOpen = false;
  }

  logout(): void {
    const returnUrl = this.getReturnUrl();
    this.clearAuthStorage();
    this.closeUserMenu();
    void this.router.navigate(['/auth'], { queryParams: { returnUrl } });
  }

  private clearAuthStorage(): void {
    const keysToRemove = ['auth.token', 'auth.email', 'auth.role', 'auth.userId'];
    for (const key of keysToRemove) {
      localStorage.removeItem(key);
    }
  }

  @HostListener('document:click')
  onDocumentClick(): void {
    this.closeUserMenu();
  }

  @HostListener('document:keydown.escape')
  onEscape(): void {
    this.closeUserMenu();
  }
}
