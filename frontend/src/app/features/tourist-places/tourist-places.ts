import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { TOURIST_PLACES_MOCK } from './tourist-places.mock';
import { TouristPlace } from './tourist-places.types';

@Component({
  selector: 'app-tourist-places',
  imports: [CommonModule, FormsModule],
  templateUrl: './tourist-places.html',
  styleUrl: './tourist-places.css',
})
export class TouristPlaces {
  private readonly http = inject(HttpClient);

  readonly places = signal<TouristPlace[]>([]);
  readonly loading = signal(true);
  readonly error = signal('');
  readonly search = signal('');
  readonly environment = signal<'ALL' | 'INTERIOR' | 'MIXED' | 'EXTERIOR'>('ALL');

  readonly filteredPlaces = computed(() => {
    const term = this.search().trim().toLowerCase();
    const environment = this.environment();

    return this.places().filter((place) => {
      const city = place.location?.city ?? '';
      const categoryNames = place.categories?.map((category) => category.name).join(' ') ?? '';
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
        this.places.set(places ?? []);
        this.loading.set(false);
      },
      error: () => {
        // Fallback to mocks so the UI can be tested without backend connectivity.
        this.places.set(TOURIST_PLACES_MOCK);
        this.loading.set(false);
      },
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
    const path = place.album?.photos?.find((photo) => photo.filePath)?.filePath;

    if (!path) {
      return this.fallbackImages[index % this.fallbackImages.length];
    }

    if (path.startsWith('http') || path.startsWith('/')) {
      return path;
    }

    return `/${path}`;
  }

  getCategories(place: TouristPlace): string[] {
    const categories = place.categories?.map((category) => category.name).filter(Boolean) ?? [];
    return categories.length > 0 ? categories.slice(0, 2) : ['Cultural'];
  }
}
