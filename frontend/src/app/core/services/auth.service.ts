import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface LoginResponse {
  token: string;
  type: string;
  email: string;
  role: string;
}

export interface RegisterRequest {
  name?: string;
  document?: string;
  phoneNumber?: string;
  email: string;
  password: string;
}

@Injectable({ providedIn: 'root' })
export class AuthService {
  constructor(private readonly http: HttpClient) {}

  recoverPassword(email: string): Observable<{ message: string }> {
    return this.http.post<{ message: string }>(`/api/auth/recover-password`, { email });
  }

  login(email: string, password: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`/api/auth/login`, { email, password });
  }

  register(request: RegisterRequest): Observable<{ message: string }> {
    return this.http.post<{ message: string }>(`/api/auth/register`, request);
  }

  changePassword(params: {
    token: string;
    email: string;
    currentPassword: string;
    newPassword: string;
  }): Observable<{ message: string }> {
    const headers = new HttpHeaders({ Authorization: `Bearer ${params.token}` });

    return this.http.put<{ message: string }>(
      `/api/auth/change-password`,
      {
        email: params.email,
        currentPassword: params.currentPassword,
        newPassword: params.newPassword,
      },
      { headers }
    );
  }
}
