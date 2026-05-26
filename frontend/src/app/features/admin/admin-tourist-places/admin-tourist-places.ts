import { CommonModule } from '@angular/common';
import { Component, DestroyRef, computed, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AuthTokenService } from '../../../core/services/auth-token.service';
import { TouristPlaceApiService } from '../../../core/services/tourist-place-api.service';
import {
  TOURIST_PLACE_ENVIRONMENTS,
  TOURIST_PLACE_FALLBACK_IMAGES,
  TouristPlaceEnvironment,
} from '../../../core/constants/tourist-place.constants';
import {
  filterTouristPlaces,
  getTouristPlaceCategories,
  getTouristPlaceEnvironmentLabel,
  getTouristPlaceLocation,
} from '../../../core/utils/tourist-place.utils';
import { TouristPlace } from '../../tourist-places/tourist-places.types';

@Component({
  selector: 'app-admin-tourist-places',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './admin-tourist-places.html',
  styleUrl: './admin-tourist-places.css',
})
export class AdminTouristPlaces {
  private readonly placeApi = inject(TouristPlaceApiService);
  private readonly authToken = inject(AuthTokenService);
  private readonly destroyRef = inject(DestroyRef);

  readonly places = signal<TouristPlace[]>([]);
  readonly coverPhotoByPlaceId = signal<Record<number, string>>({});
  readonly loading = signal(true);
  readonly error = signal('');
  readonly deletingId = signal<number | null>(null);

  readonly search = signal('');
  readonly environment = signal<TouristPlaceEnvironment>('ALL');

  readonly filteredPlaces = computed(() => {
    return filterTouristPlaces({
      places: this.places(),
      term: this.search(),
      environment: this.environment(),
    });
  });

  readonly environments = TOURIST_PLACE_ENVIRONMENTS;

  constructor() {
    this.loadPlaces();
  }

  loadPlaces(): void {
    this.loading.set(true);
    this.error.set('');

    this.placeApi.getPlaces().subscribe({
      next: (places) => {
        const resolvedPlaces = places ?? [];
        this.places.set(resolvedPlaces);
        this.loadCoverPhotos(resolvedPlaces);
        this.loading.set(false);
      },
      error: () => {
        this.error.set('No fue posible cargar los lugares turísticos desde el servidor.');
        this.places.set([]);
        this.coverPhotoByPlaceId.set({});
        this.loading.set(false);
      },
    });
  }

  private loadCoverPhotos(places: TouristPlace[]): void {
    this.coverPhotoByPlaceId.set({});

    this.placeApi
      .coverPhotos$(places)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(({ placeId, coverUrl }) => {
        if (!coverUrl) return;
        this.coverPhotoByPlaceId.update((current) => ({ ...current, [placeId]: coverUrl }));
      });
  }

  setEnvironment(value: TouristPlaceEnvironment): void {
    this.environment.set(value);
  }

  getLocation(place: TouristPlace): string {
    return getTouristPlaceLocation(place);
  }

  getEnvironmentLabel(environment?: TouristPlace['environment']): string {
    return getTouristPlaceEnvironmentLabel(environment);
  }

  getImage(place: TouristPlace, index: number): string {
    const cover = this.coverPhotoByPlaceId()[place.id];
    if (cover) return cover;
    return TOURIST_PLACE_FALLBACK_IMAGES[index % TOURIST_PLACE_FALLBACK_IMAGES.length];
  }

  getCategories(place: TouristPlace): string[] {
    return getTouristPlaceCategories(place);
  }

  deletePlace(placeId: number): void {
    if (this.deletingId()) return;

    if (!this.authToken.hasToken()) {
      this.error.set('Debes iniciar sesion como administrador para eliminar lugares.');
      return;
    }

    const confirmed = confirm('¿Seguro que deseas eliminar este lugar turístico?');
    if (!confirmed) return;

    this.error.set('');
    this.deletingId.set(placeId);

    this.placeApi.deletePlace(placeId, this.authToken.getAuthHeaders()).subscribe({
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
}
