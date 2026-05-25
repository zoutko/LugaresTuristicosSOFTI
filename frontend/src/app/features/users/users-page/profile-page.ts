import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService, UserResponse } from '../../../core/services/user.service';
import { AuthService } from '../../../core/services/auth.service';
import { ToastComponent, ToastVariant } from '../../../shared/toast/toast';
import { Router } from '@angular/router';


@Component({
  selector: 'app-profile-page',
  standalone: true,
  imports: [CommonModule, FormsModule, ToastComponent],
  templateUrl: './profile-page.html',
  styleUrls: ['./profile-page.css']
})
export class ProfilePage implements OnInit {
  user: UserResponse | null = null;
  loading = true;
  error = '';

  // Modal de edición
  editModalOpen = false;
  editFieldName = '';
  editFieldKey = '';
  editFieldValue = '';
  isSaving = false;

  // Toast
  toastOpen = false;
  toastMessage = '';
  toastVariant: ToastVariant = 'info';

  constructor(
    private userService: UserService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadProfile();
  }

goToRecorridosGuardados(): void {
    this.router.navigate(['/recorridos-guardados']);
}

goToChangePassword(): void {
    this.router.navigate(['/auth/change-password']);
}

goToConfiguracion(): void {
    this.router.navigate(['/']);
}

  private loadProfile(): void {
    const userIdStr = localStorage.getItem('auth.userId');
    if (!userIdStr) {
    this.error = 'No se pudo identificar al usuario. Por favor, inicie sesión nuevamente.';
    this.loading = false;
    return;
  }
    
    const userId = parseInt(userIdStr, 10);

    this.userService.getProfile(userId).subscribe({
      next: (data) => {
        this.user = data;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading profile:', err);
        this.error = 'Error al cargar el perfil';
        this.loading = false;
        this.showToast(this.error, 'error');
      }
    });
  }

  openEditField(fieldName: string, currentValue: string): void {
    this.editFieldName = fieldName;
    this.editFieldValue = currentValue;
    
    switch(fieldName) {
      case 'nombre':
        this.editFieldKey = 'name';
        break;
      case 'documento':
        this.editFieldKey = 'document';
        break;
      default:
        this.editFieldKey = fieldName;
    }
    
    this.editModalOpen = true;
  }

  closeEditModal(): void {
    if (this.isSaving) return;
    this.editModalOpen = false;
    this.editFieldValue = '';
  }

  saveEditField(): void {
    if (!this.editFieldValue.trim()) {
      this.showToast('El valor no puede estar vacío', 'error');
      return;
    }
    
    this.isSaving = true;

    const userIdStr = localStorage.getItem('auth.userId'); 
      if (!userIdStr) {
        this.error = 'No se pudo identificar al usuario. Por favor, inicie sesión nuevamente.';
        this.loading = false;
        return;
      }
    const userId = parseInt(userIdStr, 10);
    
    this.userService.updateProfileField(userId, this.editFieldKey, this.editFieldValue).subscribe({
      next: (updatedUser) => {
        this.user = updatedUser;
        this.isSaving = false;
        this.editModalOpen = false;
        this.showToast(`${this.editFieldName} actualizado correctamente`, 'success');
      },
      error: (err) => {
        console.error('Error updating field:', err);
        this.isSaving = false;
        this.showToast(`Error al actualizar ${this.editFieldName}`, 'error');
      }
    });
  }

  private showToast(message: string, variant: ToastVariant = 'info'): void {
    this.toastMessage = message;
    this.toastVariant = variant;
    this.toastOpen = true;
  }

  closeToast(): void {
    this.toastOpen = false;
  }
}