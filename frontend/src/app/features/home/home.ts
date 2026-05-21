import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

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
  imports: [CommonModule, RouterLink],
  templateUrl: './home.html',
  styleUrl: './home.css',
})
export class Home {
  private readonly http = inject(HttpClient);

  readonly featuredTours = signal<Tour[]>([]);
  readonly loading = signal(true);

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
        this.loading.set(false);
      },
      error: () => {
        this.featuredTours.set([]);
        this.loading.set(false);
      },
    });
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
    return categories.length > 0 ? categories.slice(0, 2) : ['Historico', 'Recreativo'];
  }

  formatPrice(price: number): string {
    return new Intl.NumberFormat('es-CO', {
      style: 'currency',
      currency: 'COP',
      maximumFractionDigits: 0,
    }).format(price);
  }
}
