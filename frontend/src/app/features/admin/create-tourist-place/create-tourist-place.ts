import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, DestroyRef, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { forkJoin, map, of, switchMap } from 'rxjs';
import { AuthTokenService } from '../../../core/services/auth-token.service';
import { LocationCatalogService } from '../../../core/services/location-catalog.service';

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
  private readonly authToken = inject(AuthTokenService);
  private readonly locationCatalog = inject(LocationCatalogService);
  private readonly destroyRef = inject(DestroyRef);

  readonly trackByIndex = (index: number): number => index;

  readonly countries = signal<string[]>([]);
  readonly departments = signal<string[]>([]);
  readonly cities = signal<string[]>([]);
  readonly loadingCountries = signal(false);
  readonly loadingDepartments = signal(false);
  readonly loadingCities = signal(false);

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
  country = '';
  latitude: number | null = null;
  longitude: number | null = null;
  newCategoryName = '';

  constructor() {
    this.loadCountries();
    this.loadCategories();
  }

  departmentOptions(): string[] {
    return this.departments();
  }

  cityOptions(): string[] {
    return this.cities();
  }

  onCountryChange(value: string): void {
    this.country = value;
    this.department = '';
    this.city = '';
    this.departments.set([]);
    this.cities.set([]);

    const country = value?.trim();
    if (!country) return;

    this.loadingDepartments.set(true);
    this.locationCatalog
      .getDepartments(country)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (departments) => {
          this.departments.set(departments ?? []);
          this.loadingDepartments.set(false);
        },
        error: () => {
          this.departments.set([]);
          this.loadingDepartments.set(false);
        },
      });
  }

  onDepartmentChange(value: string): void {
    this.department = value;
    this.city = '';
    this.cities.set([]);

    const country = this.country?.trim();
    const department = value?.trim();
    if (!country || !department) return;

    this.loadingCities.set(true);
    this.locationCatalog
      .getCities(country, department)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (cities) => {
          const list = (cities ?? []).filter(Boolean);
          if (list.length === 0) {
            this.cities.set([department]);
            this.city = department;
          } else {
            this.cities.set(list);
          }
          this.loadingCities.set(false);
        },
        error: () => {
          this.cities.set([department]);
          this.city = department;
          this.loadingCities.set(false);
        },
      });
  }

  private loadCountries(): void {
    this.loadingCountries.set(true);

    this.locationCatalog
      .getCountries()
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (countries) => {
          this.countries.set(countries ?? []);
          this.loadingCountries.set(false);
        },
        error: () => {
          this.countries.set([]);
          this.loadingCountries.set(false);
        },
      });
  }

  loadCategories(): void {
    this.loadingCategories.set(true);

    this.http.get<Category[]>('/api/categories', { headers: this.authToken.getAuthHeaders() }).subscribe({
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

    if (!this.authToken.hasToken()) {
      this.error.set('Debes iniciar sesion como administrador para crear lugares.');
      return;
    }

    if (!this.name.trim() || !this.city.trim() || !this.department.trim() || !this.country.trim()) {
      this.error.set('Completa nombre, ciudad, departamento y pais.');
      return;
    }

    this.saving.set(true);
    const headers = this.authToken.getAuthHeaders();
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
}
