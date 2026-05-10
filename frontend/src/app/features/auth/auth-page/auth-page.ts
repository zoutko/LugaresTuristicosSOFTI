import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';

import { AuthApiService } from '../auth-api.service';
import { RecoverPasswordModalComponent } from '../recover-password-modal/recover-password-modal';

@Component({
  selector: 'app-auth-page',
  standalone: true,
  imports: [CommonModule, FormsModule, RecoverPasswordModalComponent],
  templateUrl: './auth-page.html',
  styleUrl: './auth-page.css',
})
export class AuthPage {

  loginEmail = '';
  loginPassword = '';
  loginErrorMessage = '';
  loginSuccessMessage = '';
  isLoggingIn = false;

  registerName = '';
  registerEmail = '';
  registerPhoneNumber = '';
  registerDocument = '';
  registerPassword = '';
  registerConfirmPassword = '';
  registerErrorMessage = '';
  registerSuccessMessage = '';
  isRegistering = false;

  recoverModalOpen = false;
  isRecovering = false;
  recoverErrorMessage = '';

  constructor(
    private readonly router: Router,
    private readonly authApi: AuthApiService
  ) {}

  openRecoverModal(): void {
    this.recoverErrorMessage = '';
    this.recoverModalOpen = true;
  }

  closeRecoverModal(): void {
    if (this.isRecovering) return;
    this.recoverModalOpen = false;
  }

  submitRecoverPassword(email: string): void {
    this.isRecovering = true;
    this.recoverErrorMessage = '';

    this.authApi.recoverPassword(email).subscribe({
      next: async () => {
        this.isRecovering = false;
        this.recoverModalOpen = false;
        await this.router.navigate(['/auth/change-password'], { queryParams: { email } });
      },
      error: () => {
        this.isRecovering = false;
        this.recoverErrorMessage = 'No fue posible enviar la contraseña temporal. Inténtalo nuevamente.';
      },
    });
  }

  submitLogin(): void {
    if (this.isLoggingIn) return;

    this.loginErrorMessage = '';
    this.loginSuccessMessage = '';

    const email = this.loginEmail.trim();
    const password = this.loginPassword;

    if (!email || !password) {
      this.loginErrorMessage = 'Completa correo y contraseña.';
      return;
    }

    this.isLoggingIn = true;

    this.authApi.login(email, password).subscribe({
      next: async (res) => {
        this.isLoggingIn = false;

        if (res?.token) {
          localStorage.setItem('auth.token', res.token);
          localStorage.setItem('auth.email', res.email);
          localStorage.setItem('auth.role', res.role);
          this.loginSuccessMessage = 'Inicio de sesión exitoso.';
          // En este proyecto aún no hay una ruta principal definida.
          // Se deja la sesión iniciada y se muestra el mensaje de éxito.
        } else {
          this.loginErrorMessage = 'Respuesta inválida del servidor.';
        }
      },
      error: () => {
        this.isLoggingIn = false;
        this.loginErrorMessage = 'No fue posible iniciar sesión. Verifica los datos.';
      },
    });
  }

  submitRegister(): void {
    if (this.isRegistering) return;

    this.registerErrorMessage = '';
    this.registerSuccessMessage = '';

    const name = this.registerName.trim();
    const email = this.registerEmail.trim();
    const phoneNumber = this.registerPhoneNumber.trim();
    const document = this.registerDocument.trim();
    const password = this.registerPassword;

    if (!name || !email || !document || !password) {
      this.registerErrorMessage = 'Completa los campos obligatorios.';
      return;
    }

    if (password !== this.registerConfirmPassword) {
      this.registerErrorMessage = 'Las contraseñas no coinciden.';
      return;
    }

    this.isRegistering = true;

    this.authApi
      .register({ name, email, document, phoneNumber: phoneNumber || undefined, password })
      .subscribe({
        next: () => {
          this.isRegistering = false;
          this.registerSuccessMessage = 'Registro exitoso. Ya puedes iniciar sesión.';
          this.loginEmail = email;
          this.loginPassword = '';
        },
        error: () => {
          this.isRegistering = false;
          this.registerErrorMessage = 'No fue posible registrarse. Verifica los datos.';
        },
      });
  }
}
