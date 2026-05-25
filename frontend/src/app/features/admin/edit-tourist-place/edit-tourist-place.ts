import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, DestroyRef, computed, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { forkJoin, of } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';
import { TouristPlace, TouristPlaceEnvironment } from '../../tourist-places/tourist-places.types';
import { AuthTokenService } from '../../../core/services/auth-token.service';
import { LocationCatalogService } from '../../../core/services/location-catalog.service';

type Environment = TouristPlaceEnvironment;

interface Category {
  id: number;
  name: string;
}

@Component({
  selector: 'app-edit-tourist-place',
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './edit-tourist-place.html',
  styleUrl: './edit-tourist-place.css',
})
export class EditTouristPlace {
  private readonly http = inject(HttpClient);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly authToken = inject(AuthTokenService);
  private readonly locationCatalog = inject(LocationCatalogService);
  private readonly destroyRef = inject(DestroyRef);

  readonly categories = signal<Category[]>([]);
  readonly selectedCategoryIds = signal<number[]>([]);
  readonly newCategories = signal<string[]>([]);

  readonly loading = signal(true);
  readonly loadingCategories = signal(true);
  readonly saving = signal(false);
  readonly message = signal('');
  readonly error = signal('');

  readonly countries = signal<string[]>([]);
  readonly departments = signal<string[]>([]);
  readonly cities = signal<string[]>([]);
  readonly loadingCountries = signal(false);
  readonly loadingDepartments = signal(false);
  readonly loadingCities = signal(false);

  readonly placeId = signal<number | null>(null);

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

  readonly hasToken = computed(() => this.authToken.hasToken());

  constructor() {
    this.loadCountries();

    this.route.paramMap
      .pipe(
        map((params) => Number(params.get('id'))),
        switchMap((id) => {
          if (!Number.isFinite(id) || id <= 0) {
            this.error.set('El lugar solicitado no es valido.');
            this.loading.set(false);
            return of(null);
          }

          this.placeId.set(id);
          this.loading.set(true);
          this.error.set('');
          this.message.set('');

          return forkJoin({
            place: this.http.get<TouristPlace>(`/api/places/${id}`).pipe(catchError(() => of(null))),
            categories: this.http
              .get<Category[]>('/api/categories', { headers: this.authToken.getAuthHeaders() })
              .pipe(catchError(() => of([]))),
          });
        }),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe((result) => {
        if (!result) return;

        const { place, categories } = result;
        this.categories.set(categories ?? []);
        this.loadingCategories.set(false);

        if (!place) {
          this.error.set('No fue posible cargar el lugar para editar.');
          this.loading.set(false);
          return;
        }

        this.name = place.name ?? '';
        this.description = place.description ?? '';
        this.duration = place.duration ?? '';
        this.environment = (place.environment ?? 'EXTERIOR') as Environment;

        this.city = place.location?.city ?? '';
        this.department = place.location?.department ?? '';
        this.country = place.location?.country ?? '';
        this.latitude = place.location?.latitude ?? null;
        this.longitude = place.location?.longitude ?? null;

        this.syncDepartmentsAndCitiesFromCurrentSelection();

        const categoryIds = this.resolveCategoryIds(place.categories ?? [], categories ?? []);
        this.selectedCategoryIds.set(categoryIds);

        this.loading.set(false);
      });
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
          const list = countries ?? [];
          const current = this.country?.trim();
          this.countries.set(current && !list.includes(current) ? [current, ...list] : list);
          this.loadingCountries.set(false);
        },
        error: () => {
          const current = this.country?.trim();
          this.countries.set(current ? [current] : []);
          this.loadingCountries.set(false);
        },
      });
  }

  private syncDepartmentsAndCitiesFromCurrentSelection(): void {
    const country = this.country?.trim();
    const department = this.department?.trim();
    const city = this.city?.trim();
    if (!country) return;

    this.loadingDepartments.set(true);
    this.locationCatalog
      .getDepartments(country)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (departments) => {
          const deptList = departments ?? [];
          this.departments.set(deptList.includes(department) ? deptList : department ? [department, ...deptList] : deptList);
          this.loadingDepartments.set(false);

          if (!department) return;

          this.loadingCities.set(true);
          this.locationCatalog
            .getCities(country, department)
            .pipe(takeUntilDestroyed(this.destroyRef))
            .subscribe({
              next: (cities) => {
                const cityList = (cities ?? []).filter(Boolean);
                if (cityList.length === 0) {
                  this.cities.set([department]);
                  if (!this.city?.trim()) this.city = department;
                } else {
                  this.cities.set(
                    cityList.includes(city) ? cityList : city ? [city, ...cityList] : cityList
                  );
                }
                this.loadingCities.set(false);
              },
              error: () => {
                this.cities.set(city ? [city] : [department]);
                if (!this.city?.trim()) this.city = department;
                this.loadingCities.set(false);
              },
            });
        },
        error: () => {
          this.departments.set(department ? [department] : []);
          this.loadingDepartments.set(false);
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

  submit(): void {
    if (this.saving()) return;

    this.error.set('');
    this.message.set('');

    if (!this.authToken.hasToken()) {
      this.error.set('Debes iniciar sesion como administrador para editar lugares.');
      return;
    }

    const id = this.placeId();
    if (!id) {
      this.error.set('No se encontro el lugar a editar.');
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
          const categoryIds = [...this.selectedCategoryIds(), ...createdCategories.map((category) => category.id)];

          return this.http.patch<TouristPlace>(
            `/api/places/${id}`,
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
        })
      )
      .subscribe({
        next: async () => {
          this.saving.set(false);
          this.message.set('Lugar turistico actualizado correctamente.');
          await this.router.navigate(['/admin/lugares']);
        },
        error: () => {
          this.saving.set(false);
          this.error.set('No fue posible actualizar el lugar. Verifica los datos y permisos.');
        },
      });
  }

  private resolveCategoryIds(placeCategoryNames: string[], categories: Category[]): number[] {
    const normalizedNames = new Set(
      (placeCategoryNames ?? []).filter(Boolean).map((name) => name.trim().toLowerCase())
    );

    return categories
      .filter((category) => normalizedNames.has(category.name.trim().toLowerCase()))
      .map((category) => category.id);
  }
}
