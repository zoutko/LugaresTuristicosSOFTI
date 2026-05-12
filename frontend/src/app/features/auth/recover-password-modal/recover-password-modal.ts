import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, OnChanges, Output, SimpleChanges } from '@angular/core';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-recover-password-modal',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './recover-password-modal.html',
  styleUrl: './recover-password-modal.css',
})
export class RecoverPasswordModalComponent implements OnChanges {
  @Input() open = false;
  @Input() isSubmitting = false;
  @Input() errorMessage = '';
  @Input() initialEmail = '';

  @Output() closed = new EventEmitter<void>();
  @Output() submitted = new EventEmitter<string>();

  email = '';
  localError = '';

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['open']?.currentValue === true) {
      this.email = this.initialEmail || '';
      this.localError = '';
    }

    if (changes['errorMessage']) {
      this.localError = '';
    }
  }

  close(): void {
    if (this.isSubmitting) return;
    this.closed.emit();
  }

  submit(): void {
    const email = this.email.trim();

    if (!email) {
      this.localError = 'Ingresa un correo electrónico.';
      return;
    }

    this.localError = '';
    this.submitted.emit(email);
  }
}
