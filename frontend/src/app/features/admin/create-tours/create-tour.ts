import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, DestroyRef, inject, Input, signal, OnInit } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { FormsModule } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { forkJoin, of, switchMap } from 'rxjs';
import { AuthTokenService } from '../../../core/services/auth-token.service';
import { LocationCatalogService } from '../../../core/services/location-catalog.service';

type Environment = 'INTERIOR' | 'MIXED' | 'EXTERIOR';

interface AlbumPhoto {
  filePath: string;
  description: string;
  id?: number;
  position?: number;
}

interface AlbumResponse {
  id?: number;
  title?: string;
  currentIndex?: number;
  totalPhotos?: number;
  currentPhoto?: AlbumPhoto;
  photos?: AlbumPhoto[];
}

interface Category {
  id: number;
  name: string;
}

interface Place {
  id: number;
  name: string;
}

interface ImageInput {
  url: string;
  description: string;
}

interface TourResponse {
  id: number;
  name: string;
  description: string;
  recommendations: string;
  environment: string;
  price: number;
  location: string;      // ← Es un string, ej: "Medellín, Antioquia, Colombia"
  meetingPoint: string;   // ← Es un string, ej: "Parque de las Luces, Medellín, Antioquia, Colombia"
  categories: string[];   // ← Array de nombres de categorías
  itinerary: Array<{ touristPlaceId: number; position?: number }>;
  album?: any;
  tourOffer?: any;
}

@Component({
  selector: 'app-create-tour',
  imports: [CommonModule, FormsModule],
  templateUrl: './create-tour.html',
  styleUrl: './create-tour.css',
})
export class CreateTour implements OnInit {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly route = inject(ActivatedRoute);
  private readonly authToken = inject(AuthTokenService);
  private readonly locationCatalog = inject(LocationCatalogService);
  private readonly destroyRef = inject(DestroyRef);

  readonly trackByIndex = (index: number): number => index;

  // Modo edición
  isEditMode = false;
  tourId: number | null = null;

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

  // Lugares disponibles para itinerario
  readonly places = signal<Place[]>([]);
  readonly loadingPlaces = signal(false);
  readonly selectedPlaceIds = signal<number[]>([]);

  // Galería de imágenes
  readonly images = signal<ImageInput[]>([{ url: '', description: '' }]);

  // Estado del formulario
  readonly saving = signal(false);
  readonly loading = signal(false);
  readonly uploadingImages = signal(false);
  readonly message = signal('');
  readonly error = signal('');

  // Tour creado
  createdTourId: number | null = null;
  tourCreated = false;

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
  environment: Environment = 'EXTERIOR';
  price = 0;
  city = '';
  department = '';
  country = '';
  meetingPointCity = '';
  meetingPointDepartment = '';
  meetingPointCountry = '';
  meetingPointAddress = '';
  newCategoryName = '';

  constructor() {
    this.loadCountries();
    this.loadCategories();
    this.loadPlaces();
  }

  ngOnInit(): void {
    this.tourId = this.route.snapshot.params['id'];
    if (this.tourId) {
      this.isEditMode = true;
      this.loadTourData();
    }
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
      error: (err) => {
        console.error('Error loading categories:', err);
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

  // ============ LUGARES PARA ITINERARIO ============
  loadPlaces(): void {
    this.loadingPlaces.set(true);

    this.http.get<Place[]>('/api/places', { headers: this.authToken.getAuthHeaders() }).subscribe({
      next: (places) => {
        this.places.set(places ?? []);
        this.loadingPlaces.set(false);
      },
      error: () => {
        this.places.set([]);
        this.loadingPlaces.set(false);
      },
    });
  }

  togglePlace(placeId: number, checked: boolean): void {
    this.selectedPlaceIds.update((current) =>
      checked ? [...current, placeId] : current.filter((id) => id !== placeId)
    );
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

  // ============ CARGAR DATOS PARA EDICIÓN ============
  loadTourData(): void {
  this.loading.set(true);
  this.error.set('');

  this.http.get<TourResponse>(`/api/tours/${this.tourId}`, { headers: this.authToken.getAuthHeaders() }).subscribe({
    next: (tour) => {
      console.log('=== TOUR RECIBIDO ===', tour);
      
      // Datos básicos
      this.name = tour.name;
      this.description = tour.description || '';
      this.recommendations = tour.recommendations || '';
      this.environment = tour.environment as Environment;
      this.price = tour.price;

      // ============ UBICACIÓN (string) ============
      if (tour.location && typeof tour.location === 'string') {
        const parts = tour.location.split(',').map(p => p.trim());
        this.city = parts[0] || '';
        this.department = parts[1] || '';
        this.country = parts[2] || '';
        console.log('Ubicación parseada:', { city: this.city, department: this.department, country: this.country });
        
        // Cargar selectores
        if (this.country) {
          this.onCountryChange(this.country);
          if (this.department) {
            setTimeout(() => {
              this.onDepartmentChange(this.department);
            }, 300);
          }
        }
      }

      // ============ PUNTO DE ENCUENTRO (string) ============
      if (tour.meetingPoint && typeof tour.meetingPoint === 'string') {
        const parts = tour.meetingPoint.split(',').map(p => p.trim());
        if (parts.length === 4) {
          this.meetingPointAddress = parts[0] || '';
          this.meetingPointCity = parts[1] || '';
          this.meetingPointDepartment = parts[2] || '';
          this.meetingPointCountry = parts[3] || '';
        } else if (parts.length === 3) {
          this.meetingPointCity = parts[0] || '';
          this.meetingPointDepartment = parts[1] || '';
          this.meetingPointCountry = parts[2] || '';
          this.meetingPointAddress = '';
        } else if (parts.length === 2) {
          this.meetingPointCity = parts[0] || '';
          this.meetingPointCountry = parts[1] || '';
          this.meetingPointDepartment = '';
          this.meetingPointAddress = '';
        } else {
          this.meetingPointCity = parts[0] || '';
          this.meetingPointDepartment = '';
          this.meetingPointCountry = '';
          this.meetingPointAddress = '';
        }
        console.log('Punto de encuentro parseado:', {
          address: this.meetingPointAddress,
          city: this.meetingPointCity,
          department: this.meetingPointDepartment,
          country: this.meetingPointCountry
        });
      }

      // ============ CATEGORÍAS ============
      // tour.categories es un array de strings (nombres)
      // Necesitamos convertir nombres a IDs
      if (tour.categories && Array.isArray(tour.categories)) {
        const categoryIds: number[] = [];
        this.categories().forEach(cat => {
          if (tour.categories.includes(cat.name)) {
            categoryIds.push(cat.id);
          }
        });
        this.selectedCategoryIds.set(categoryIds);
        console.log('Categorías seleccionadas IDs:', categoryIds);
      }

      // ============ ITINERARIO (LUGARES) ============
      if (tour.itinerary && Array.isArray(tour.itinerary)) {
        const placeIds = tour.itinerary
          .map(item => item.touristPlaceId)
          .filter(id => id);
        this.selectedPlaceIds.set(placeIds);
        console.log('Lugares seleccionados IDs:', placeIds);
      }

      // ============ IMÁGENES ============
      // ============ IMÁGENES ============
if (tour.album && tour.album.photos && Array.isArray(tour.album.photos) && tour.album.photos.length > 0) {
  const imagesList = tour.album.photos.map((photo: AlbumPhoto) => ({
    url: photo.filePath || '',
    description: photo.description || ''
  }));
  this.images.set(imagesList);
  console.log('Imágenes cargadas:', imagesList);
}

      this.loading.set(false);
    },
    error: (err) => {
      console.error('Error loading tour:', err);
      this.error.set('No fue posible cargar el recorrido.');
      this.loading.set(false);
    },
  });
}
  // ============ ACTUALIZAR TOUR (EDICIÓN) ============
  updateTour(): void {
    if (this.saving()) return;

    this.error.set('');
    this.message.set('');

    if (!this.authToken.hasToken()) {
      this.error.set('Debes iniciar sesión como administrador.');
      return;
    }

    if (!this.name.trim() || !this.city.trim() || !this.department.trim() || !this.country.trim()) {
      this.error.set('Completa nombre, ciudad, departamento y país.');
      return;
    }

    if (this.selectedPlaceIds().length === 0) {
      this.error.set('Selecciona al menos un lugar para el itinerario.');
      return;
    }

    if (this.selectedCategoryIds().length === 0 && this.newCategories().length === 0) {
      this.error.set('Selecciona o crea al menos una categoría.');
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

          const requestBody = {
            name: this.name.trim(),
            description: this.description.trim(),
            recommendations: this.recommendations.trim(),
            environment: this.environment,
            price: this.price,
            categoryIds: categoryIds,
            itineraryPlaceIds: this.selectedPlaceIds(),
            location: {
              city: this.city.trim(),
              department: this.department.trim(),
              country: this.country.trim(),
            },
            meetingPoint: {
              city: this.meetingPointCity.trim(),
              department: this.meetingPointDepartment.trim(),
              country: this.meetingPointCountry.trim(),
              address: this.meetingPointAddress.trim(),
            },
          };

          return this.http.patch(`/api/tours/${this.tourId}`, requestBody, { headers });
        })
      )
      .subscribe({
        next: () => {
          this.saving.set(false);
          this.message.set('Recorrido actualizado correctamente.');
          setTimeout(() => {
            this.router.navigate(['/admin/recorridos']);
          }, 2000);
        },
        error: (err) => {
          console.error('Error updating tour:', err);
          this.saving.set(false);
          this.error.set('No fue posible actualizar el recorrido.');
        },
      });
  }

  // ============ CREAR TOUR ============
  createTour(): void {
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

    if (this.selectedPlaceIds().length === 0) {
      this.error.set('Selecciona al menos un lugar para el itinerario.');
      return;
    }

    if (this.selectedCategoryIds().length === 0 && this.newCategories().length === 0) {
      this.error.set('Selecciona o crea al menos una categoría.');
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

          const requestBody = {
            name: this.name.trim(),
            description: this.description.trim(),
            recommendations: this.recommendations.trim(),
            environment: this.environment,
            price: this.price,
            categoryIds: categoryIds,
            itineraryPlaceIds: this.selectedPlaceIds(),
            location: {
              city: this.city.trim(),
              department: this.department.trim(),
              country: this.country.trim(),
            },
            meetingPoint: {
              city: this.meetingPointCity.trim(),
              department: this.meetingPointDepartment.trim(),
              country: this.meetingPointCountry.trim(),
              address: this.meetingPointAddress.trim(),
            },
          };

          return this.http.post<TourResponse>('/api/tours', requestBody, { headers });
        })
      )
      .subscribe({
        next: (tour) => {
          this.saving.set(false);
          this.createdTourId = tour.id;
          this.tourCreated = true;
          this.message.set(`Recorrido "${tour.name}" creado exitosamente. Ahora puedes agregar imágenes.`);
          this.images.set([{ url: '', description: '' }]);
        },
        error: (err) => {
          console.error('Error creating tour:', err);
          this.saving.set(false);
          this.error.set('No fue posible crear el recorrido. Verifica los datos y permisos de administrador.');
        },
      });
  }

  // Subir imágenes del tour creado
  uploadImages(): void {
    if (this.uploadingImages()) return;
    if (!this.createdTourId) return;

    const validImages = this.images()
      .filter(img => img.url.trim())
      .map(img => ({
        url: img.url.trim(),
        description: img.description.trim()
      }));

    if (validImages.length === 0) {
      this.error.set('Agrega al menos una URL de imagen válida.');
      return;
    }

    this.uploadingImages.set(true);
    this.error.set('');
    const headers = this.authToken.getAuthHeaders();

    const imageRequests = validImages.map(image =>
      this.http.post(`/api/tours/${this.createdTourId}/media/photos`, image, { headers })
    );

    forkJoin(imageRequests).subscribe({
      next: () => {
        this.uploadingImages.set(false);
        this.message.set('Imágenes agregadas exitosamente.');
        setTimeout(() => {
          this.router.navigate(['/admin/recorridos']);
        }, 2000);
      },
      error: () => {
        this.uploadingImages.set(false);
        this.error.set('Error al subir las imágenes. Intenta nuevamente.');
      }
    });
  }

  // ============ ENVÍO ============
  onSubmit(): void {
    if (this.isEditMode) {
      this.updateTour();
    } else {
      this.createTour();
    }
  }

  // ============ NAVEGACIÓN ============
  goBack(): void {
    this.router.navigate(['/admin/recorridos']);
  }

  goToAdminTours(): void {
    this.router.navigate(['/admin/recorridos']);
  }

  resetForm(): void {
    this.tourCreated = false;
    this.createdTourId = null;
    this.name = '';
    this.description = '';
    this.recommendations = '';
    this.environment = 'EXTERIOR';
    this.price = 0;
    this.city = '';
    this.department = '';
    this.country = '';
    this.meetingPointCity = '';
    this.meetingPointDepartment = '';
    this.meetingPointCountry = '';
    this.meetingPointAddress = '';
    this.selectedCategoryIds.set([]);
    this.newCategories.set([]);
    this.selectedPlaceIds.set([]);
    this.images.set([{ url: '', description: '' }]);
    this.message.set('');
    this.error.set('');
  }
}