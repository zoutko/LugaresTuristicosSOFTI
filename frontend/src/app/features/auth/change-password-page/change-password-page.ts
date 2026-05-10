import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { firstValueFrom } from 'rxjs';

import { AuthApiService } from '../auth-api.service';

@Component({
  selector: 'app-change-password-page',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './change-password-page.html',
  styleUrl: './change-password-page.css',
})
export class ChangePasswordPage {
  email = '';
  temporaryPassword = '';
  newPassword = '';
  confirmPassword = '';

  isSubmitting = false;
  errorMessage = '';

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly authApi: AuthApiService
  ) {
    const emailParam = this.route.snapshot.queryParamMap.get('email');
    if (emailParam) this.email = emailParam;
  }

  async submit(): Promise<void> {
    this.errorMessage = '';

    const email = this.email.trim();
    const temporaryPassword = this.temporaryPassword;
    const newPassword = this.newPassword;

    if (!email || !temporaryPassword || !newPassword) {
      this.errorMessage = 'Completa todos los campos.';
      return;
    }

    if (newPassword !== this.confirmPassword) {
      this.errorMessage = 'Las contraseñas no coinciden.';
      return;
    }

    this.isSubmitting = true;

    try {
      const loginResponse = await firstValueFrom(this.authApi.login(email, temporaryPassword));
      if (!loginResponse?.token) {
        this.errorMessage = 'No fue posible validar la contraseña temporal.';
        return;
      }

      await firstValueFrom(
        this.authApi.changePassword({
          token: loginResponse.token,
          email,
          currentPassword: temporaryPassword,
          newPassword,
        })
      );

      await this.router.navigate(['/auth']);
    } catch {
      this.errorMessage = 'No fue posible cambiar la contraseña. Verifica los datos e inténtalo de nuevo.';
    } finally {
      this.isSubmitting = false;
    }
  }

  cancel(): void {
    this.router.navigate(['/auth']);
  }
}
