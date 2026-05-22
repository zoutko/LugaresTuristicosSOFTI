import { CommonModule } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, DestroyRef, computed, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { from, of } from 'rxjs';
import { catchError, map, mergeMap } from 'rxjs/operators';
import { TouristPlace, TouristPlaceAlbum } from '../../tourist-places/tourist-places.types';

@Component({
  selector: 'app-admin-tourist-places',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './admin-tourist-places.html',
  styleUrl: './admin-tourist-places.css',
})
export class AdminTouristPlaces {
  private readonly http = inject(HttpClient);
  private readonly destroyRef = inject(DestroyRef);

  readonly places = signal<TouristPlace[]>([]);
  readonly coverPhotoByPlaceId = signal<Record<number, string>>({});
  readonly loading = signal(true);
  readonly error = signal('');
  readonly deletingId = signal<number | null>(null);

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

  deletePlace(placeId: number): void {
    if (this.deletingId()) return;

    const token = localStorage.getItem('auth.token');
    if (!token) {
      this.error.set('Debes iniciar sesion como administrador para eliminar lugares.');
      return;
    }

    const confirmed = confirm('¿Seguro que deseas eliminar este lugar turistico?');
    if (!confirmed) return;

    this.error.set('');
    this.deletingId.set(placeId);

    this.http.delete<void>(`/api/places/${placeId}`, { headers: this.authHeaders() }).subscribe({
      next: () => {
        this.places.update((current) => current.filter((place) => place.id !== placeId));
        this.coverPhotoByPlaceId.update((current) => {
          const { [placeId]: _removed, ...rest } = current;
          return rest;
        });
        this.deletingId.set(null);
      },
      error: () => {
        this.error.set('No fue posible eliminar el lugar. Verifica permisos de administrador.');
        this.deletingId.set(null);
      },
    });
  }

  private authHeaders(): HttpHeaders {
    const token = localStorage.getItem('auth.token');
    return token ? new HttpHeaders({ Authorization: `Bearer ${token}` }) : new HttpHeaders();
  }
}
