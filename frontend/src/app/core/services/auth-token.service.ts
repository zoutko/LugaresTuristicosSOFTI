import { HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class AuthTokenService {
  private readonly storageKey = 'auth.token';

  getToken(): string | null {
    return localStorage.getItem(this.storageKey);
  }

  hasToken(): boolean {
    return Boolean(this.getToken());
  }

  /**
   * Returns Authorization header when token exists; otherwise an empty header set.
   */
  getAuthHeaders(): HttpHeaders {
    const token = this.getToken();
    return token ? new HttpHeaders({ Authorization: `Bearer ${token}` }) : new HttpHeaders();
  }
}
