import { Component, OnInit, inject, signal, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { AuthTokenService } from '../../../core/services/auth-token.service';
import { TourCardComponent } from '../../../shared/tour-card/tour-card';
import { ToastComponent, ToastVariant } from '../../../shared/toast/toast';
import { TourCard } from '../../../core/models/tour-model';

@Component({
  selector: 'app-admin-tours-list',
  standalone: true,
  imports: [CommonModule, FormsModule, TourCardComponent, ToastComponent],
  templateUrl: './admin-tours-list.html',
  styleUrls: ['./admin-tours-list.css']
})
export class AdminToursListComponent implements OnInit {
  private readonly http = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly authToken = inject(AuthTokenService);

  allTours = signal<TourCard[]>([]);
  searchTerm = signal('');
  maxPrice = signal(0);
  priceMin = 0;
  priceMax = 0;
  priceStep = 10000;
  
  availableCategories: string[] = [];
  
  environments = ['INTERIOR', 'EXTERIOR', 'MIXTO'];
  
  selectedCategories = signal<string[]>([]);
  selectedEnvironments = signal<string[]>([]);

  loading = signal(true);
  error = signal('');

  toastOpen = false;
  toastMessage = '';
  toastVariant: ToastVariant = 'info';
  showDeleteModal = false;
  deleteTourId: number | null = null;

filteredTours = computed(() => {
  let result = this.allTours();
  
  const term = this.searchTerm().toLowerCase();
  if (term) {
    result = result.filter(t => 
      t.name.toLowerCase().includes(term) || 
      t.city.toLowerCase().includes(term)
    );
  }
  
  const price = this.maxPrice();
  if (price > 0) {
    result = result.filter(t => t.price <= price);
  }
  
  const selectedCats = this.selectedCategories();
  if (selectedCats.length > 0) {
    result = result.filter(tour =>
      selectedCats.some(cat => tour.categories.includes(cat))
    );
  }
  
  const selectedEnvs = this.selectedEnvironments();
  if (selectedEnvs.length > 0) {
    result = result.filter(tour =>
      selectedEnvs.includes(tour.environment)
    );
  }
  
  return result;
});


  ngOnInit(): void {
    this.loadTours();
  }

  
    loadTours(): void {
  this.loading.set(true);
  this.error.set('');

  this.http.get<TourCard[]>('/api/tours', { headers: this.authToken.getAuthHeaders() }).subscribe({
    next: (tours) => {
      this.allTours.set(tours);  
      this.extractCategories();
      this.syncPriceRange();
      this.loading.set(false);
    },
    error: (err) => {
      console.error('Error loading tours:', err);
      this.error.set('No fue posible cargar los recorridos.');
      this.loading.set(false);
    }
  });
}
  
 extractCategories(): void {
  const categoriesSet = new Set<string>();
  this.allTours().forEach(tour => {  // ← Usar ()
    tour.categories?.forEach(cat => categoriesSet.add(cat));
  });
  this.availableCategories = Array.from(categoriesSet).sort();
}

 
 syncPriceRange(): void {
  const prices = this.allTours().map(t => t.price).filter(p => Number.isFinite(p));  // ← Usar ()
  if (prices.length > 0) {
    this.priceMin = Math.min(...prices);
    this.priceMax = Math.max(...prices);
    this.priceStep = this.computePriceStep(this.priceMax);
    this.maxPrice.set(this.priceMax); 
  }
}

  private computePriceStep(max: number): number {
    if (max <= 50000) return 1000;
    if (max <= 200000) return 5000;
    return 10000;
  }

  environmentLabel(environment: string): string {
    const labels: Record<string, string> = {
      INTERIOR: 'Interior',
      EXTERIOR: 'Exterior',
      MIXTO: 'Mixto'
    };
    return labels[environment] ?? environment;
  }

  onSearchChange(): void {

  this.searchTerm.set(this.searchTerm());
}

onPriceChange(): void {
 
  this.maxPrice.set(this.maxPrice());
}


 toggleCategory(category: string): void {
  this.selectedCategories.update(current => {
    const index = current.indexOf(category);
    if (index > -1) {
      return current.filter((_, i) => i !== index);
    } else {
      return [...current, category];
    }
  });
}

toggleEnvironment(environment: string): void {
  this.selectedEnvironments.update(current => {
    const index = current.indexOf(environment);
    if (index > -1) {
      return current.filter((_, i) => i !== index);
    } else {
      return [...current, environment];
    }
  });
}


clearFilters(): void {
  this.searchTerm.set('');
  this.maxPrice.set(this.priceMax);
  this.selectedCategories.set([]);
  this.selectedEnvironments.set([]);
}

  editTour(tourId: number): void {
    this.router.navigate(['/admin/recorridos', tourId, 'editar']);
  }

  deleteTour(tourId: number): void {
  if (confirm('¿Estás seguro de eliminar este recorrido?')) {
    this.http.delete(`/api/tours/${tourId}`, { headers: this.authToken.getAuthHeaders() }).subscribe({
      next: () => {
        this.showToast('Recorrido eliminado exitosamente', 'success');
        this.loadTours();
      },
      error: () => {
        this.showToast('Error al eliminar el recorrido', 'error');
      }
    });
  }
}

openDeleteModal(tourId: number): void {
  this.deleteTourId = tourId;
  this.showDeleteModal = true;
}

closeDeleteModal(): void {
  this.showDeleteModal = false;
  this.deleteTourId = null;
}

confirmDelete(): void {
  if (!this.deleteTourId) return;
  
  this.http.delete(`/api/tours/${this.deleteTourId}`, { headers: this.authToken.getAuthHeaders() }).subscribe({
    next: () => {
      
      const currentTours = this.allTours();
      const filteredTours = currentTours.filter(t => t.id !== this.deleteTourId);
      this.allTours.set(filteredTours);  // ← Usar .set()
      
      this.showToast('Recorrido eliminado exitosamente', 'success');
      this.closeDeleteModal();
    },
    error: (err) => {
      console.error('Error:', err);
      this.showToast('Error al eliminar el recorrido', 'error');
      this.closeDeleteModal();
    }
  });
}
  goToCreate(): void {
    this.router.navigate(['/admin/recorridos/crear']);
  }

  private showToast(message: string, variant: ToastVariant = 'info'): void {
    this.toastMessage = message;
    this.toastVariant = variant;
    this.toastOpen = true;
  }

  closeToast(): void {
    this.toastOpen = false;
  }
  
}

