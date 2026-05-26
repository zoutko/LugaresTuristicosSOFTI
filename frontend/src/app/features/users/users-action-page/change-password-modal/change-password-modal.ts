import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-change-password-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './change-password-modal.html',
  styleUrl: './change-password-modal.css',
})
export class ChangePasswordModalComponent implements OnChanges {
  @Input() open = false;
  @Input() isSubmitting = false;
  @Input() errorMessage = '';
  @Input() initialEmail = '';

  @Output() closed = new EventEmitter<void>();
  @Output() submitted = new EventEmitter<{
    currentPassword: string;
    newPassword: string;
  }>();

  currentPassword = '';
  newPassword = '';
  confirmPassword = '';
  localError = '';

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['open']?.currentValue === true) {
      this.reset();
    }
    if (changes['errorMessage']) {
      this.localError = '';
    }
  }

  reset(): void {
    this.currentPassword = '';
    this.newPassword = '';
    this.confirmPassword = '';
    this.localError = '';
  }

  close(): void {
    if (this.isSubmitting) return;
    this.reset();
    this.closed.emit();
  }

  submit(): void {
    // Validaciones
    if (!this.currentPassword) {
      this.localError = 'Ingrese su contraseña actual.';
      return;
    }

    if (!this.newPassword) {
      this.localError = 'Ingrese una nueva contraseña.';
      return;
    }

    if (this.newPassword.length < 6) {
      this.localError = 'La nueva contraseña debe tener al menos 6 caracteres.';
      return;
    }

    if (this.newPassword !== this.confirmPassword) {
      this.localError = 'Las contraseñas no coinciden.';
      return;
    }

    if (this.currentPassword === this.newPassword) {
      this.localError = 'La nueva contraseña debe ser diferente a la actual.';
      return;
    }

    this.localError = '';
    this.submitted.emit({
      currentPassword: this.currentPassword,
      newPassword: this.newPassword,
    });
  }
}