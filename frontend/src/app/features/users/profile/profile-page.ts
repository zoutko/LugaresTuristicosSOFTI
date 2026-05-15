import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { UserService, UserResponse } from '../../../core/services/user.service';
import { ToastComponent, ToastVariant } from '../../../shared/toast/toast';

@Component({
  selector: 'app-profile-page',
  standalone: true,
  imports: [CommonModule, ToastComponent],
  templateUrl: './profile-page.html',
  styleUrls: ['./profile-page.css']
})
export class ProfilePage implements OnInit {
  user: UserResponse | null = null;
  loading = true;
  error = '';

  toastOpen = false;
  toastMessage = '';
  toastVariant: ToastVariant = 'info';

  constructor(private userService: UserService) {}

  ngOnInit(): void {
    this.loadProfile();
  }

  private loadProfile(): void {
    const userId = 1; // TEMPORAL - Reemplazar después
    
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

  private showToast(message: string, variant: ToastVariant = 'info'): void {
    this.toastMessage = message;
    this.toastVariant = variant;
    this.toastOpen = true;
  }

  closeToast(): void {
    this.toastOpen = false;
  }
}