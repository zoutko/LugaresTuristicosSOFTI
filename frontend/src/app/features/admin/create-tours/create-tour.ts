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

interface ItineraryItem {
  placeName: string;
  description: string;
  duration?: string;
}

interface PriceOption {
  category: string;
  amount: number;
}

interface ImageInput {
  url: string;
  description: string;
}

@Component({
  selector: 'app-create-tour',
  imports: [CommonModule, FormsModule],
  templateUrl: './create-tour.html',
  styleUrl: './create-tour.css',
})
export class CreateTour {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly authToken = inject(AuthTokenService);
  private readonly locationCatalog = inject(LocationCatalogService);
  private readonly destroyRef = inject(DestroyRef);

  readonly trackByIndex = (index: number): number => index;

  // Ubicación
  readonly countries = signal<string[]>([]);
  readonly departments = signal<string[]>([]);
  readonly cities = signal<string[]>([]);
  readonly loadingCountries = signal(false);
  readonly loadingDepartments = signal(false);
  readonly loadingCities = signal(false);

  // Categorías
  readonly categories = signal<Category[]>([]);
  readonly selectedCategoryIds = signal<number[]>([]);
  readonly newCategories = signal<string[]>([]);
  readonly loadingCategories = signal(true);

  // Itinerario (lugares)
  readonly itinerary = signal<ItineraryItem[]>([{ placeName: '', description: '' }]);
  
  // Galería de imágenes
  readonly images = signal<ImageInput[]>([{ url: '', description: '' }]);
  
  // Tarifas
  readonly priceOptions = signal<PriceOption[]>([{ category: '', amount: 0 }]);

  // Estado del formulario
  readonly saving = signal(false);
  readonly message = signal('');
  readonly error = signal('');

  // Entornos
  readonly environments: { value: Environment; label: string }[] = [
    { value: 'EXTERIOR', label: 'Exterior' },
    { value: 'INTERIOR', label: 'Interior' },
    { value: 'MIXED', label: 'Mixto' },
  ];

  // Campos del formulario
  name = '';
  description = '';
  recommendations = '';
  duration = '';
  environment: Environment = 'EXTERIOR';
  meetingPoint = '';
  pricePerPerson = 0;
  city = '';
  department = '';
  country = '';
  meetingPointCity = '';
  meetingPointDepartment = '';
  meetingPointCountry = '';
  newCategoryName = '';

  constructor() {
    this.loadCountries();
    this.loadCategories();
  }

  // ============ UBICACIÓN ============
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

  // ============ PUNTO DE ENCUENTRO ============
  onMeetingPointCountryChange(value: string): void {
    this.meetingPointCountry = value;
    this.meetingPointDepartment = '';
    this.meetingPointCity = '';
  }

  onMeetingPointDepartmentChange(value: string): void {
    this.meetingPointDepartment = value;
    this.meetingPointCity = '';
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

  // ============ CATEGORÍAS ============
  loadCategories(): void {
    this.loadingCategories.set(true);

    this.http.get<Category[]>('/api/categories', { headers: this.authToken.getAuthHeaders() }).subscribe({
      next: (categories) => {
        this.categories.set(categories ?? []);
        this.loadingCategories.set(false);
      },
      error: () => {
        this.error.set('No fue posible cargar las categorías.');
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

  // ============ ITINERARIO ============
  addItineraryItem(): void {
    this.itinerary.update((current) => [...current, { placeName: '', description: '' }]);
  }

  updateItineraryItem(index: number, field: keyof ItineraryItem, value: string): void {
    this.itinerary.update((current) =>
      current.map((item, i) => (i === index ? { ...item, [field]: value } : item))
    );
  }

  removeItineraryItem(index: number): void {
    this.itinerary.update((current) => current.filter((_, i) => i !== index));
  }

  // ============ TARIFAS ============
  addPriceOption(): void {
    this.priceOptions.update((current) => [...current, { category: '', amount: 0 }]);
  }

  updatePriceOption(index: number, field: keyof PriceOption, value: string | number): void {
    this.priceOptions.update((current) =>
      current.map((option, i) => (i === index ? { ...option, [field]: value } : option))
    );
  }

  removePriceOption(index: number): void {
    this.priceOptions.update((current) => current.filter((_, i) => i !== index));
  }

  // ============ IMÁGENES ============
  addImage(): void {
    this.images.update((current) => [...current, { url: '', description: '' }]);
  }

  updateImage(index: number, field: keyof ImageInput, value: string): void {
    this.images.update((current) =>
      current.map((image, i) => (i === index ? { ...image, [field]: value } : image))
    );
  }

  removeImage(index: number): void {
    this.images.update((current) => current.filter((_, i) => i !== index));
  }

  // ============ SUBMIT ============
  submit(): void {
    if (this.saving()) return;

    this.error.set('');
    this.message.set('');

    if (!this.authToken.hasToken()) {
      this.error.set('Debes iniciar sesión como administrador para crear recorridos.');
      return;
    }

    if (!this.name.trim() || !this.city.trim() || !this.department.trim() || !this.country.trim()) {
      this.error.set('Completa nombre, ciudad, departamento y país.');
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

          // Filtrar itinerario válido
          const validItinerary = this.itinerary()
            .filter(item => item.placeName.trim())
            .map(item => ({
              placeName: item.placeName.trim(),
              description: item.description.trim(),
              duration: item.duration
            }));

          // Filtrar tarifas válidas
          const validPrices = this.priceOptions()
            .filter(price => price.category.trim() && price.amount > 0);

          // Filtrar imágenes válidas
          const validImages = this.images()
            .filter(img => img.url.trim())
            .map(img => ({
              url: img.url.trim(),
              description: img.description.trim()
            }));

          return this.http.post('/api/tours', {
            name: this.name.trim(),
            description: this.description.trim(),
            recommendations: this.recommendations.trim(),
            duration: this.duration.trim(),
            environment: this.environment,
            location: {
              city: this.city.trim(),
              department: this.department.trim(),
              country: this.country.trim(),
            },
            meetingPoint: {
              city: this.meetingPointCity.trim(),
              department: this.meetingPointDepartment.trim(),
              country: this.meetingPointCountry.trim(),
              address: this.meetingPoint.trim()
            },
            pricePerPerson: this.pricePerPerson,
            categoryIds,
            itinerary: validItinerary,
            prices: validPrices,
            images: validImages
          }, { headers });
        })
      )
      .subscribe({
        next: async (tour) => {
          this.saving.set(false);
          this.message.set('Recorrido turístico creado correctamente.');
          await this.router.navigate(['/admin/tours']);
        },
        error: () => {
          this.saving.set(false);
          this.error.set('No fue posible crear el recorrido. Verifica los datos y permisos de administrador.');
        },
      });
  }
}