import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, computed, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { TourCardComponent } from '../../shared/tour-card/tour-card';
import { TourCard } from '../../core/models/tour-model';
import { SavedToursService } from '../../core/services/saved-tours-service';
import { AuthTokenService } from '../../core/services/auth-token.service';
import { ToastComponent, ToastVariant } from '../../shared/toast/toast';

interface TourPhoto {
  filePath: string;
  fileName?: string | null;
  description?: string | null;
}

interface TourAlbum {
  currentPhoto: TourPhoto | null;
  photos: TourPhoto[];
}

interface Tour {
  id: number;
  name: string;
  categories?: string[];
  price: number;
  location?: string | null;
  album?: TourAlbum | null;
}

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [CommonModule, RouterLink, TourCardComponent, ToastComponent],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly savedToursService = inject(SavedToursService);
  private readonly authTokenService = inject(AuthTokenService);

  readonly featuredTours = signal<Tour[]>([]);
  readonly loading = signal(true);

  savedIds = new Set<number>();
  toastOpen = false;
  toastMessage = '';
  toastVariant: ToastVariant = 'info';

  readonly featuredTourCards = computed<TourCard[]>(() =>
    this.featuredTours().map((tour, index) => this.mapToTourCard(tour, index))
  );

  private readonly fallbackImages = [
    'https://cartagenaplay.com/wp-content/uploads/9008013895_5a53127df8_o-scaled.jpg',
    'https://radionacional-v3.s3.amazonaws.com/s3fs-public/node/article/field_image/MONSERRATE.jpg',
    'https://img.travesiasdigital.com/2019/03/pieza-museo-del-oro.jpg',
  ];

  constructor() {
    this.loadFeaturedTours();
  }

  private loadFeaturedTours(): void {
    this.http.get<Tour[]>('/api/tours/filter').subscribe({
      next: (tours) => {
        this.featuredTours.set((tours ?? []).slice(0, 3));
        this.loadSavedIdsIfNeeded();
        this.loading.set(false);
      },
      error: () => {
        this.featuredTours.set([]);
        this.loading.set(false);
      },
    });
  }

  private loadSavedIdsIfNeeded(): void {
    const tokenExists = this.authTokenService.hasToken();
    const userIdStr = localStorage.getItem('auth.userId');
    const userId = userIdStr ? Number(userIdStr) : null;

    if (!tokenExists || !userId) return;

    this.savedToursService.getSavedTours(userId).subscribe({
      next: (saved) => {
        this.savedIds = new Set(saved.map((s) => s.id ?? s));
      },
      error: () => {
        this.savedIds = new Set<number>();
      },
    });
  }

  onSaveTour(tourId: number): void {
    const userIdStr = localStorage.getItem('auth.userId');
    const userId = userIdStr ? Number(userIdStr) : null;

    if (!this.authTokenService.hasToken() || !userId) {
      this.showToast(
        'Debes iniciar sesión para guardar recorridos',
        'info'
      );
      return;
    }

    this.savedToursService.saveTour(userId, tourId).subscribe({
      next: () => this.savedIds.add(tourId),
      error: () =>
        this.showToast(
          'No fue posible guardar el recorrido',
          'error'
        ),
    });
  }

  onRemoveSavedTour(tourId: number): void {
    const userIdStr = localStorage.getItem('auth.userId');
    const userId = userIdStr ? Number(userIdStr) : null;

    if (!this.authTokenService.hasToken() || !userId) {
      this.showToast(
        'Debes iniciar sesión para quitar recorridos guardados',
        'info'
      );
      return;
    }

    this.savedToursService.removeSavedTour(userId, tourId).subscribe({
      next: () => this.savedIds.delete(tourId),
      error: () =>
        this.showToast(
          'No fue posible quitar el recorrido guardado',
          'error'
        ),
    });
  }

  private showToast(
    message: string,
    variant: ToastVariant = 'info'
  ): void {
    this.toastMessage = message;
    this.toastVariant = variant;
    this.toastOpen = true;
  }

  closeToast(): void {
    this.toastOpen = false;
  }

  getImage(tour: Tour, index: number): string {
    return (
      tour.album?.photos?.[0]?.filePath ||
      tour.album?.currentPhoto?.filePath ||
      this.fallbackImages[index % this.fallbackImages.length]
    );
  }

  getCategories(tour: Tour): string[] {
    const categories = tour.categories?.filter(Boolean) ?? [];
    return categories.slice(0, 2);
  }

  formatPrice(price: number): string {
    return new Intl.NumberFormat('es-CO', {
      style: 'currency',
      currency: 'COP',
      maximumFractionDigits: 0,
    }).format(price);
  }

  viewTour(tourId: number): void {
    this.router.navigate(['/tour', tourId]);
  }

  private mapToTourCard(tour: Tour, index: number): TourCard {
    return {
      id: tour.id,
      name: tour.name,
      city: tour.location ?? '',
      country: '',
      categories: this.getCategories(tour),
      environment: '',
      price: tour.price,
      imageUrl: this.getImage(tour, index),
    };
  }
}
