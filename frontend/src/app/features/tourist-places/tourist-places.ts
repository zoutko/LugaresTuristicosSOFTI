import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, DestroyRef, computed, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { from, of } from 'rxjs';
import { catchError, map, mergeMap } from 'rxjs/operators';
import { TouristPlace, TouristPlaceAlbum } from './tourist-places.types';

@Component({
  selector: 'app-tourist-places',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './tourist-places.html',
  styleUrl: './tourist-places.css',
})
export class TouristPlaces {
  private readonly http = inject(HttpClient);
  private readonly destroyRef = inject(DestroyRef);

  readonly places = signal<TouristPlace[]>([]);
  readonly coverPhotoByPlaceId = signal<Record<number, string>>({});
  readonly loading = signal(true);
  readonly error = signal('');
  readonly search = signal('');
  readonly environment = signal<'ALL' | 'INTERIOR' | 'MIXED' | 'EXTERIOR'>('ALL');

  readonly filteredPlaces = computed(() => {
    const term = this.search().trim().toLowerCase();
    const environment = this.environment();

    return this.places().filter((place) => {
      const city = place.location?.city ?? '';
      const categoryNames = place.categories?.join(' ') ?? '';
      const matchesTerm = `${place.name} ${city} ${categoryNames}`.toLowerCase().includes(term);
      const matchesEnvironment = environment === 'ALL' || place.environment === environment;

      return matchesTerm && matchesEnvironment;
    });
  });

  readonly environments = [
    { value: 'ALL', label: 'Todos' },
    { value: 'EXTERIOR', label: 'Exterior' },
    { value: 'INTERIOR', label: 'Interior' },
    { value: 'MIXED', label: 'Mixto' },
  ] as const;

  private readonly fallbackImages = [
    'https://images.unsplash.com/photo-1583531352515-8884af319dc1?auto=format&fit=crop&w=900&q=80',
    'https://images.unsplash.com/photo-1448375240586-882707db888b?auto=format&fit=crop&w=900&q=80',
    'https://images.unsplash.com/photo-1512813195386-6cf811ad3542?auto=format&fit=crop&w=900&q=80',
    'https://images.unsplash.com/photo-1564013799919-ab600027ffc6?auto=format&fit=crop&w=900&q=80',
  ];

  constructor() {
    this.loadPlaces();
  }

  loadPlaces(): void {
    this.loading.set(true);
    this.error.set('');

    this.http.get<TouristPlace[]>('/api/places').subscribe({
      next: (places) => {
        const resolvedPlaces = places ?? [];
        this.places.set(resolvedPlaces);
        this.loadCoverPhotos(resolvedPlaces);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('No fue posible cargar los lugares turisticos desde el servidor.');
        this.places.set([]);
        this.coverPhotoByPlaceId.set({});
        this.loading.set(false);
      },
    });
  }

  private loadCoverPhotos(places: TouristPlace[]): void {
    this.coverPhotoByPlaceId.set({});

    from(places)
      .pipe(
        mergeMap(
          (place) =>
            this.http.get<TouristPlaceAlbum>(`/api/places/${place.id}/media/album`).pipe(
              map((album) => ({
                placeId: place.id,
                coverUrl: album.photos?.[0]?.filePath ?? album.currentPhoto?.filePath ?? '',
              })),
              catchError(() => of({ placeId: place.id, coverUrl: '' }))
            ),
          6
        ),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe(({ placeId, coverUrl }) => {
        if (!coverUrl) return;
        this.coverPhotoByPlaceId.update((current) => ({ ...current, [placeId]: coverUrl }));
      });
  }

  setEnvironment(value: 'ALL' | 'INTERIOR' | 'MIXED' | 'EXTERIOR'): void {
    this.environment.set(value);
  }

  getLocation(place: TouristPlace): string {
    const city = place.location?.city;
    const country = place.location?.country;

    if (city && country) {
      return `${city}, ${country}`;
    }

    return city || country || 'Ubicacion por confirmar';
  }

  getEnvironmentLabel(environment?: TouristPlace['environment']): string {
    const labels = {
      INTERIOR: 'Interior',
      MIXED: 'Mixto',
      EXTERIOR: 'Exterior',
    };

    return environment ? labels[environment] : 'Ambiente';
  }

  getImage(place: TouristPlace, index: number): string {
    const cover = this.coverPhotoByPlaceId()[place.id];
    if (cover) return cover;
    return this.fallbackImages[index % this.fallbackImages.length];
  }

  getCategories(place: TouristPlace): string[] {
    const categories = place.categories?.filter(Boolean) ?? [];
    return categories.length > 0 ? categories.slice(0, 3) : ['Cultural'];
  }
}
