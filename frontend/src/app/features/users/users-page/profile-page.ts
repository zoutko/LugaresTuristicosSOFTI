import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { UserService, UserResponse } from '../../../core/services/user.service';
import { AuthService } from '../../../core/services/auth.service';
import { ChangePasswordModalComponent } from '../users-action-page/change-password-modal/change-password-modal';
import { ToastComponent, ToastVariant } from '../../../shared/toast/toast';
import { Router } from '@angular/router';

@Component({
  selector: 'app-profile-page',
  standalone: true,
  imports: [CommonModule, FormsModule, ToastComponent, ChangePasswordModalComponent],
  templateUrl: './profile-page.html',
  styleUrls: ['./profile-page.css']
})
export class ProfilePage implements OnInit {
  user: UserResponse | null = null;
  loading = true;
  error = '';

  isAdmin = false;

  // Modal de edición
  editModalOpen = false;
  editFieldName = '';
  editFieldKey = '';
  editFieldValue = '';
  isSaving = false;

  // Modal de confirmación al eliminar cuenta
  showDeleteConfirmModal = false;

  // Modal de cambio de contraseña
  changePasswordModalOpen = false;

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
    this.checkUserRole();
    this.loadProfile();
  }

  checkUserRole(): void {
    const role = localStorage.getItem('auth.role');
    this.isAdmin = role === 'ADMINISTRATOR';
  }

  goToRecorridosGuardados(): void {
    this.router.navigate(['/recorridos-guardados']);
  }

  goToChangePassword(): void {
    this.openChangePasswordModal();
  }

  handleChangePassword(data: { currentPassword: string; newPassword: string }): void {
    const token = localStorage.getItem('auth.token');
    const email = localStorage.getItem('auth.email');
    
    if (!token || !email) {
      this.showToast('No se pudo identificar al usuario', 'error');
      this.closeChangePasswordModal();
      return;
    }
    this.closeChangePasswordModal();
    this.authService.changePassword({
      token,
      email,
      currentPassword: data.currentPassword,
      newPassword: data.newPassword
    }).subscribe({
      next: () => {
        this.showToast('Contraseña cambiada exitosamente', 'success');
        this.closeChangePasswordModal();
      },
      error: () => {
        this.showToast('Error al cambiar la contraseña. Verifique su contraseña actual.', 'error');
      }
    });
  }

  goToConfiguracion(): void {
    this.openDeleteConfirmModal();
  }

  confirmDeleteAccount(): void {
    this.closeDeleteConfirmModal();
    
    const userIdStr = localStorage.getItem('auth.userId');
    if (!userIdStr) {
      this.showToast('No se pudo identificar al usuario', 'error');
      return;
    }
    
    const userId = parseInt(userIdStr, 10);
    
    this.userService.deleteAccount(userId).subscribe({
      next: () => {
        localStorage.clear();
        this.showToast('Cuenta eliminada exitosamente', 'success');
        setTimeout(() => {
          this.router.navigate(['/auth']);
        }, 2000);
      },
      error: (err) => {
        console.error('Error deleting account:', err);
        this.showToast('Error al eliminar la cuenta', 'error');
      }
    });
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

  goToCreateTours(): void {
    this.router.navigate(['/admin/recorridos/crear']);
  }

  goToCreatePlaces(): void {
    this.router.navigate(['/admin/lugares/crear']);
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

    const userIdStr = localStorage.getItem('auth.userId');
    if (!userIdStr) {
      this.error = 'No se pudo identificar al usuario. Por favor, inicie sesión nuevamente.';
      this.loading = false;
      return;
    }

    this.isSaving = true;
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

  openDeleteConfirmModal(): void {
    this.showDeleteConfirmModal = true;
  }

  closeDeleteConfirmModal(): void {
    this.showDeleteConfirmModal = false;
  }

  openChangePasswordModal(): void {
    this.changePasswordModalOpen = true;
  }

  closeChangePasswordModal(): void {
    this.changePasswordModalOpen = false;
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