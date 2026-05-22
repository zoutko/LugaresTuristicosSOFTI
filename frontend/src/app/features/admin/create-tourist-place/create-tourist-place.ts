import { CommonModule } from '@angular/common';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { forkJoin, map, of, switchMap } from 'rxjs';

type Environment = 'INTERIOR' | 'MIXED' | 'EXTERIOR';

interface Category {
  id: number;
  name: string;
}

interface CreatedPlace {
  id: number;
  name: string;
}

interface PhotoInput {
  filePath: string;
  description: string;
}

@Component({
  selector: 'app-create-tourist-place',
  imports: [CommonModule, FormsModule],
  templateUrl: './create-tourist-place.html',
  styleUrl: './create-tourist-place.css',
})
export class CreateTouristPlace {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);

  readonly categories = signal<Category[]>([]);
  readonly selectedCategoryIds = signal<number[]>([]);
  readonly newCategories = signal<string[]>([]);
  readonly activities = signal<string[]>(['']);
  readonly photos = signal<PhotoInput[]>([{ filePath: '', description: '' }]);
  readonly loadingCategories = signal(true);
  readonly saving = signal(false);
  readonly message = signal('');
  readonly error = signal('');

  readonly environments: { value: Environment; label: string }[] = [
    { value: 'EXTERIOR', label: 'Exterior' },
    { value: 'INTERIOR', label: 'Interior' },
    { value: 'MIXED', label: 'Mixto' },
  ];

  name = '';
  description = '';
  duration = '';
  environment: Environment = 'EXTERIOR';
  city = '';
  department = '';
  country = 'Colombia';
  latitude: number | null = null;
  longitude: number | null = null;
  newCategoryName = '';

  constructor() {
    this.loadCategories();
  }

  loadCategories(): void {
    this.loadingCategories.set(true);

    this.http.get<Category[]>('/api/categories', { headers: this.authHeaders() }).subscribe({
      next: (categories) => {
        this.categories.set(categories ?? []);
        this.loadingCategories.set(false);
      },
      error: () => {
        this.error.set('No fue posible cargar las categorias.');
        this.loadingCategories.set(false);
      },
    });
  }

  toggleCategory(categoryId: number, checked: boolean): void {
    this.selectedCategoryIds.update((current) =>
      checked ? [...current, categoryId] : current.filter((id) => id !== categoryId)
    );
  }

  addNewCategory(): void {
    const name = this.newCategoryName.trim();
    if (!name) return;

    const existsInSelected = this.newCategories().some((category) => category.toLowerCase() === name.toLowerCase());
    const existsInLoaded = this.categories().some((category) => category.name.toLowerCase() === name.toLowerCase());

    if (!existsInSelected && !existsInLoaded) {
      this.newCategories.update((current) => [...current, name]);
    }

    this.newCategoryName = '';
  }

  removeNewCategory(name: string): void {
    this.newCategories.update((current) => current.filter((category) => category !== name));
  }

  addActivity(): void {
    this.activities.update((current) => [...current, '']);
  }

  updateActivity(index: number, value: string): void {
    this.activities.update((current) => current.map((activity, i) => (i === index ? value : activity)));
  }

  removeActivity(index: number): void {
    this.activities.update((current) => current.filter((_, i) => i !== index));
  }

  addPhoto(): void {
    this.photos.update((current) => [...current, { filePath: '', description: '' }]);
  }

  updatePhoto(index: number, field: keyof PhotoInput, value: string): void {
    this.photos.update((current) =>
      current.map((photo, i) => (i === index ? { ...photo, [field]: value } : photo))
    );
  }

  removePhoto(index: number): void {
    this.photos.update((current) => current.filter((_, i) => i !== index));
  }

  submit(): void {
    if (this.saving()) return;

    this.error.set('');
    this.message.set('');

    const token = localStorage.getItem('auth.token');
    if (!token) {
      this.error.set('Debes iniciar sesion como administrador para crear lugares.');
      return;
    }

    if (!this.name.trim() || !this.city.trim() || !this.department.trim() || !this.country.trim()) {
      this.error.set('Completa nombre, ciudad, departamento y pais.');
      return;
    }

    this.saving.set(true);
    const headers = this.authHeaders();
    const newCategoryRequests = this.newCategories().map((name) =>
      this.http.post<Category>('/api/categories', { name }, { headers })
    );

    const categoriesRequest = newCategoryRequests.length > 0 ? forkJoin(newCategoryRequests) : of([]);

    categoriesRequest
      .pipe(
        switchMap((createdCategories) => {
          const categoryIds = [
            ...this.selectedCategoryIds(),
            ...createdCategories.map((category) => category.id),
          ];

          return this.http.post<CreatedPlace>(
            '/api/places',
            {
              name: this.name.trim(),
              description: this.description.trim(),
              duration: this.duration.trim(),
              environment: this.environment,
              location: {
                city: this.city.trim(),
                department: this.department.trim(),
                country: this.country.trim(),
                latitude: this.latitude,
                longitude: this.longitude,
              },
              categoryIds,
            },
            { headers }
          );
        }),
        switchMap((place) => {
          const activityRequests = this.activities()
            .map((activity) => activity.trim())
            .filter(Boolean)
            .map((description) =>
              this.http.post(`/api/places/${place.id}/activities`, { description }, { headers })
            );

          const photoRequests = this.photos()
            .map((photo) => ({
              filePath: photo.filePath.trim(),
              description: photo.description.trim(),
            }))
            .filter((photo) => photo.filePath)
            .map((photo) => this.http.post(`/api/places/${place.id}/media/photos`, photo, { headers }));

          const requests = [...activityRequests, ...photoRequests];
          return requests.length > 0 ? forkJoin(requests).pipe(map(() => place)) : of(place);
        })
      )
      .subscribe({
        next: async (place) => {
          this.saving.set(false);
          this.message.set('Lugar turistico creado correctamente.');
          await this.router.navigate(['/lugares', place.id]);
        },
        error: () => {
          this.saving.set(false);
          this.error.set('No fue posible crear el lugar. Verifica los datos y permisos de administrador.');
        },
      });
  }

  private authHeaders(): HttpHeaders {
    const token = localStorage.getItem('auth.token');
    return token ? new HttpHeaders({ Authorization: `Bearer ${token}` }) : new HttpHeaders();
  }
}
