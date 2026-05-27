import { Component, OnInit, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { AuthTokenService } from '../../../core/services/auth-token.service';
import { ToastComponent, ToastVariant } from '../../../shared/toast/toast';

interface Tour {
  id: number;
  name: string;
  description: string;
  price: number;
  location: {
    city: string;
    department: string;
    country: string;
  };
  environment: string;
}

@Component({
  selector: 'app-admin-tours-list',
  standalone: true,
  imports: [CommonModule, ToastComponent],
  templateUrl: './admin-tours-list.html',
  styleUrls: ['./admin-tours-list.css']
})
export class AdminToursListComponent implements OnInit {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly authToken = inject(AuthTokenService);

  tours = signal<Tour[]>([]);
  loading = signal(true);
  error = signal('');

  toastOpen = false;
  toastMessage = '';
  toastVariant: ToastVariant = 'info';

  ngOnInit(): void {
    this.loadTours();
  }

  loadTours(): void {
    this.loading.set(true);
    this.error.set('');

    this.http.get<Tour[]>('/api/tours', { headers: this.authToken.getAuthHeaders() }).subscribe({
      next: (tours) => {
        this.tours.set(tours);
        this.loading.set(false);
      },
      error: (err) => {
        console.error('Error loading tours:', err);
        this.error.set('No fue posible cargar los recorridos.');
        this.loading.set(false);
      }
    });
  }

  editTour(tourId: number): void {
    this.router.navigate(['/admin/recorridos', tourId, 'editar']);
  }

  deleteTour(tourId: number): void {
    if (confirm('¿Estás seguro de eliminar este recorrido? Esta acción no se puede deshacer.')) {
      this.http.delete(`/api/tours/${tourId}`, { headers: this.authToken.getAuthHeaders() }).subscribe({
        next: () => {
          this.showToast('Recorrido eliminado exitosamente', 'success');
          this.loadTours();
        },
        error: (err) => {
          console.error('Error deleting tour:', err);
          this.showToast('Error al eliminar el recorrido', 'error');
        }
      });
    }
  }

  goToCreate(): void {
    this.router.navigate(['/admin/recorridos/crear']);
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