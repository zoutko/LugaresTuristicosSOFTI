import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { TourService } from '../../../core/services/tour.service';
import { TourCardComponent } from '../../../shared/tour-card/tour-card';
import { TourCard } from '../../../core/models/tour-model';
import { ToastComponent, ToastVariant } from '../../../shared/toast/toast';

@Component({
  selector: 'app-tour-list',
  standalone: true,
  imports: [CommonModule, FormsModule, TourCardComponent, ToastComponent],
  templateUrl: './tour-list.html',
  styleUrls: ['./tour-list.css'],
})
export class TourListComponent implements OnInit {
  allTours: TourCard[] = [];
  filteredTours: TourCard[] = [];
  loading = true;
  error = '';

  searchTerm = '';
  maxPrice = 0;

  priceMin = 0;
  priceMax = 0;
  priceStep = 10000;

  availableCategories: string[] = [];
  selectedCategories: string[] = [];

  environments = ['INTERIOR', 'EXTERIOR', 'MIXTO'];
  selectedEnvironments: string[] = [];

  toastOpen = false;
  toastMessage = '';
  toastVariant: ToastVariant = 'info';

  constructor(
    private tourService: TourService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadTours();
  }

  private loadTours(): void {
    this.loading = true;
    this.error = '';

    this.tourService.getToursAsCards().subscribe({
      next: (tours) => {
        this.allTours = tours;
        this.filteredTours = tours;
        this.syncPriceRange();
        this.extractAvailableCategories();
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading tours:', err);
        this.error = 'Error al cargar los recorridos';
        this.loading = false;
        this.showToast(this.error, 'error');
      },
    });
  }

  private extractAvailableCategories(): void {
    const categoriesSet = new Set<string>();
    this.allTours.forEach((tour) => {
      tour.categories.forEach((cat) => categoriesSet.add(cat));
    });
    this.availableCategories = Array.from(categoriesSet).sort();
  }

  private syncPriceRange(): void {
    const prices = this.allTours.map((tour) => tour.price).filter((price) => Number.isFinite(price));
    const min = prices.length > 0 ? Math.min(...prices) : 0;
    const max = prices.length > 0 ? Math.max(...prices) : 0;

    this.priceMin = min;
    this.priceMax = max;
    this.priceStep = this.computePriceStep(max);

    if (this.maxPrice === 0 || this.maxPrice > this.priceMax) {
      this.maxPrice = this.priceMax;
    }

    if (this.maxPrice < this.priceMin) {
      this.maxPrice = this.priceMin;
    }
  }

  private computePriceStep(max: number): number {
    if (max <= 50000) return 1000;
    if (max <= 200000) return 5000;
    return 10000;
  }

  applyFilters(): void {
    const normalizedSearch = this.searchTerm.trim().toLowerCase();

    this.filteredTours = this.allTours.filter((tour) => {
      if (normalizedSearch && !tour.name.toLowerCase().includes(normalizedSearch)) {
        return false;
      }

      if (tour.price > this.maxPrice) {
        return false;
      }

      if (this.selectedCategories.length > 0) {
        const hasCategory = this.selectedCategories.some((cat) => tour.categories.includes(cat));
        if (!hasCategory) return false;
      }

      if (this.selectedEnvironments.length > 0) {
        if (!this.selectedEnvironments.includes(tour.environment)) {
          return false;
        }
      }

      return true;
    });
  }

  onSearchChange(): void {
    this.applyFilters();
  }

  onPriceChange(): void {
    this.applyFilters();
  }

  toggleCategory(category: string): void {
    const index = this.selectedCategories.indexOf(category);
    if (index > -1) {
      this.selectedCategories.splice(index, 1);
    } else {
      this.selectedCategories.push(category);
    }
    this.applyFilters();
  }

  toggleEnvironment(environment: string): void {
    const index = this.selectedEnvironments.indexOf(environment);
    if (index > -1) {
      this.selectedEnvironments.splice(index, 1);
    } else {
      this.selectedEnvironments.push(environment);
    }
    this.applyFilters();
  }

  environmentLabel(environment: string): string {
    const labels: Record<string, string> = {
      INTERIOR: 'Interior',
      EXTERIOR: 'Exterior',
      MIXTO: 'Mixto',
    };
    return labels[environment] ?? environment;
  }

  clearFilters(): void {
    this.searchTerm = '';
    this.maxPrice = this.priceMax;
    this.selectedCategories = [];
    this.selectedEnvironments = [];
    this.applyFilters();
  }

  viewTour(tourId: number): void {
    this.router.navigate(['/tour', tourId]);
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
