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

  allTours: TourCard[] = [];
  
  // Filtros
  searchTerm = '';
  maxPrice = 0;
  priceMin = 0;
  priceMax = 0;
  priceStep = 10000;
  
  availableCategories: string[] = [];
  selectedCategories: string[] = [];
  
  environments = ['INTERIOR', 'EXTERIOR', 'MIXTO'];
  selectedEnvironments: string[] = [];

  loading = signal(true);
  error = signal('');

  toastOpen = false;
  toastMessage = '';
  toastVariant: ToastVariant = 'info';

  filteredTours = computed(() => {
    let result = this.allTours;
    
    if (this.searchTerm) {
      const term = this.searchTerm.toLowerCase();
      result = result.filter(t => 
        t.name.toLowerCase().includes(term) || 
        t.city.toLowerCase().includes(term)
      );
    }
    
    if (this.maxPrice > 0) {
      result = result.filter(t => t.price <= this.maxPrice);
    }
    
    if (this.selectedCategories.length > 0) {
      result = result.filter(tour =>
        this.selectedCategories.some(cat => tour.categories.includes(cat))
      );
    }
    
    if (this.selectedEnvironments.length > 0) {
      result = result.filter(tour =>
        this.selectedEnvironments.includes(tour.environment)
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
        this.allTours = tours;
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

  private extractCategories(): void {
    const categoriesSet = new Set<string>();
    this.allTours.forEach(tour => {
      tour.categories?.forEach(cat => categoriesSet.add(cat));
    });
    this.availableCategories = Array.from(categoriesSet).sort();
  }

  private syncPriceRange(): void {
    const prices = this.allTours.map(t => t.price).filter(p => Number.isFinite(p));
    if (prices.length > 0) {
      this.priceMin = Math.min(...prices);
      this.priceMax = Math.max(...prices);
      this.priceStep = this.computePriceStep(this.priceMax);
      this.maxPrice = this.priceMax;
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

  onSearchChange(): void {}
  onPriceChange(): void {}

  toggleCategory(category: string): void {
    const index = this.selectedCategories.indexOf(category);
    if (index > -1) {
      this.selectedCategories.splice(index, 1);
    } else {
      this.selectedCategories.push(category);
    }
  }

  toggleEnvironment(environment: string): void {
    const index = this.selectedEnvironments.indexOf(environment);
    if (index > -1) {
      this.selectedEnvironments.splice(index, 1);
    } else {
      this.selectedEnvironments.push(environment);
    }
  }

  clearFilters(): void {
    this.searchTerm = '';
    this.maxPrice = this.priceMax;
    this.selectedCategories = [];
    this.selectedEnvironments = [];
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