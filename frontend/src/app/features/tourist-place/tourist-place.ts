import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, computed, inject, signal } from '@angular/core';
import { DomSanitizer, SafeResourceUrl } from '@angular/platform-browser';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { catchError, forkJoin, map, of, switchMap } from 'rxjs';
import {
  TouristPlace as TouristPlaceResponse,
  TouristPlaceAlbum,
  TouristPlacePhoto,
} from '../tourist-places/tourist-places.types';

@Component({
  selector: 'app-tourist-place',
  imports: [CommonModule, RouterLink],
  templateUrl: './tourist-place.html',
  styleUrl: './tourist-place.css',
})
export class TouristPlace {
  private readonly http = inject(HttpClient);
  private readonly route = inject(ActivatedRoute);
  private readonly sanitizer = inject(DomSanitizer);

  readonly place = signal<TouristPlaceResponse | null>(null);
  readonly album = signal<TouristPlaceAlbum | null>(null);
  readonly selectedPhoto = signal<TouristPlacePhoto | null>(null);
  readonly loading = signal(true);
  readonly error = signal('');

  readonly photos = computed(() => this.album()?.photos ?? []);
  readonly categories = computed(() => {
    const categories = this.place()?.categories?.filter(Boolean) ?? [];
    return categories.length > 0 ? categories : ['Cultural'];
  });
  readonly activities = computed(() => this.place()?.activities?.filter((activity) => activity.description) ?? []);
  readonly hasCoordinates = computed(() => {
    const location = this.place()?.location;
    return typeof location?.latitude === 'number' && typeof location?.longitude === 'number';
  });
  readonly mapUrl = computed<SafeResourceUrl | null>(() => {
    const location = this.place()?.location;
    if (location?.latitude == null || location?.longitude == null) {
      return null;
    }

    const lat = location.latitude;
    const lng = location.longitude;
    const offset = 0.01;
    const bbox = [lng - offset, lat - offset, lng + offset, lat + offset].join(',');
    const url = `https://www.openstreetmap.org/export/embed.html?bbox=${bbox}&layer=mapnik&marker=${lat},${lng}`;

    return this.sanitizer.bypassSecurityTrustResourceUrl(url);
  });

  private readonly fallbackImage =
    'https://images.unsplash.com/photo-1512813195386-6cf811ad3542?auto=format&fit=crop&w=1400&q=80';

  constructor() {
    this.route.paramMap
      .pipe(
        map((params) => Number(params.get('id'))),
        switchMap((id) => {
          this.loading.set(true);
          this.error.set('');
          this.place.set(null);
          this.album.set(null);
          this.selectedPhoto.set(null);

          if (!Number.isFinite(id) || id <= 0) {
            return of({ place: null, album: null, invalid: true });
          }

          return forkJoin({
            place: this.http.get<TouristPlaceResponse>(`/api/places/${id}`),
            album: this.http
              .get<TouristPlaceAlbum>(`/api/places/${id}/media/album`)
              .pipe(catchError(() => of(null))),
          }).pipe(
            map((response) => ({ ...response, invalid: false })),
            catchError(() => of({ place: null, album: null, invalid: false }))
          );
        })
      )
      .subscribe(({ place, album, invalid }) => {
        if (invalid) {
          this.error.set('El lugar solicitado no es valido.');
          this.loading.set(false);
          return;
        }

        if (!place) {
          this.error.set('No fue posible cargar el detalle del lugar turistico.');
          this.loading.set(false);
          return;
        }

        this.place.set(place);
        this.album.set(album);
        this.selectedPhoto.set(album?.currentPhoto ?? album?.photos?.[0] ?? null);
        this.loading.set(false);
      });
  }

  getHeroImage(): string {
    return this.selectedPhoto()?.filePath || this.fallbackImage;
  }

  selectPhoto(photo: TouristPlacePhoto): void {
    this.selectedPhoto.set(photo);
  }

  getLocation(): string {
    const location = this.place()?.location;
    const parts = [location?.city, location?.department, location?.country].filter(Boolean);
    return parts.length > 0 ? parts.join(', ') : 'Ubicacion por confirmar';
  }

  getEnvironmentLabel(): string {
    const environment = this.place()?.environment;
    const labels = {
      INTERIOR: 'Interior',
      MIXED: 'Mixto',
      EXTERIOR: 'Exterior',
    };

    return environment ? labels[environment] : 'Ambiente';
  }

  getCoordinates(): string {
    const location = this.place()?.location;
    if (location?.latitude == null || location?.longitude == null) {
      return 'Coordenadas por confirmar';
    }

    return `${location.latitude.toFixed(4)}, ${location.longitude.toFixed(4)}`;
  }

  getOpenStreetMapLink(): string {
    const location = this.place()?.location;
    if (location?.latitude == null || location?.longitude == null) {
      return 'https://www.openstreetmap.org';
    }

    return `https://www.openstreetmap.org/?mlat=${location.latitude}&mlon=${location.longitude}#map=15/${location.latitude}/${location.longitude}`;
  }
}
