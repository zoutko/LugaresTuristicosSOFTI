import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { SavedToursService } from '../../../../core/services/saved-tours-service';
import { TourCardComponent } from '../../../../shared/tour-card/tour-card';
import { SavedTour, TourCard } from '../../../../core/models/tour-model';
import { ToastComponent, ToastVariant } from '../../../../shared/toast/toast';

@Component({
  selector: 'app-saved-tours-list',
  standalone: true,
  imports: [CommonModule, TourCardComponent, ToastComponent],
  templateUrl: './favorite-page.html',
  styleUrls: ['./favorite-page.css']
})
export class SavedToursListComponent implements OnInit {
  tours: TourCard[] = [];
  loading = true;
  error = '';

  toastOpen = false;
  toastMessage = '';
  toastVariant: ToastVariant = 'info';

  constructor(
    private savedToursService: SavedToursService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadSavedTours();
  }

  /*private loadSavedTours(): void {
    const userIdStr = localStorage.getItem('auth.userId'); 
    if (!userIdStr) {
      this.error = 'No se pudo identificar al usuario.';
      this.loading = false;
      return;
    }

    const userId = parseInt(userIdStr, 10);
    this.savedToursService.getSavedTours(userId).subscribe({
      next: (tours) => {
        this.tours = tours;
        this.loading = false;
      },
      error: (err) => {
        console.error('Error:', err);
        this.error = 'Error al cargar los recorridos guardados';
        this.loading = false;
        this.showToast(this.error, 'error');
      }
    });
  }*/
 private loadSavedTours(): void {
  const userIdStr = localStorage.getItem('auth.userId');
  if (!userIdStr) {
    this.error = 'No se pudo identificar al usuario.';
    this.loading = false;
    return;
  }

  const userId = parseInt(userIdStr, 10);
  this.savedToursService.getSavedToursAsCards(userId).subscribe({
    next: (tours) => {
      this.tours = tours;
      this.loading = false;
    },
    error: (err) => {
      console.error('Error:', err);
      this.error = 'Error al cargar los recorridos guardados';
      this.loading = false;
      this.showToast(this.error, 'error');
    }
  });
}

  viewTour(tourId: number): void {
    this.router.navigate(['/tour', tourId]);
  }

  removeTour(tourId: number): void {
    const userIdStr = localStorage.getItem('auth.userId');
    const userId = userIdStr ? parseInt(userIdStr, 10) : null;

    if (!userId) return;

    this.savedToursService.removeSavedTour(userId, tourId).subscribe({
      next: () => {
        this.tours = this.tours.filter(t => t.id !== tourId);
        this.showToast('Recorrido eliminado de guardados', 'success');
      },
      error: () => {
        this.showToast('Error al eliminar el recorrido', 'error');
      }
    });
  }

  goBackToProfile(): void {
    this.router.navigate(['/profile']);
  }

  goToExplorar(): void {
    this.router.navigate(['/recorridos']);
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