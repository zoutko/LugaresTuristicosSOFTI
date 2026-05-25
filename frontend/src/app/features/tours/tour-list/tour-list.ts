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
  styleUrls: ['./tour-list.css']
})
export class TourListComponent implements OnInit {
  allTours: TourCard[] = [];
  filteredTours: TourCard[] = [];
  loading = true;
  error = '';

  // Filtros
  searchTerm = '';
  maxPrice = 500000;
  
  // Filtros de categorías
  availableCategories: string[] = [];
  selectedCategories: string[] = [];
  
  // Filtros de entorno
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
    this.tourService.getToursAsCards().subscribe({
      next: (tours) => {
        this.allTours = tours;
        this.filteredTours = tours;
        this.extractAvailableCategories();
        this.loading = false;
      },
      error: (err) => {
        console.error('Error loading tours:', err);
        this.error = 'Error al cargar los recorridos';
        this.loading = false;
        this.showToast(this.error, 'error');
      }
    });
  }

  private extractAvailableCategories(): void {
    const categoriesSet = new Set<string>();
    this.allTours.forEach(tour => {
      tour.categories.forEach(cat => categoriesSet.add(cat));
    });
    this.availableCategories = Array.from(categoriesSet).sort();
  }

  applyFilters(): void {
    this.filteredTours = this.allTours.filter(tour => {
      // Filtro por nombre
      if (this.searchTerm && !tour.name.toLowerCase().includes(this.searchTerm.toLowerCase())) {
        return false;
      }
      
      // Filtro por precio
      if (tour.price > this.maxPrice) {
        return false;
      }
      
      // Filtro por categorías
      if (this.selectedCategories.length > 0) {
        const hasCategory = this.selectedCategories.some(cat => 
          tour.categories.includes(cat)
        );
        if (!hasCategory) return false;
      }
      
      // Filtro por entorno
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

  clearFilters(): void {
    this.searchTerm = '';
    this.maxPrice = 500000;
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