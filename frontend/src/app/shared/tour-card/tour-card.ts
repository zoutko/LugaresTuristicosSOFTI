import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TourCard } from '../../core/models/tour-model';
import { Router } from '@angular/router';

@Component({
  selector: 'app-tour-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './tour-card.html',
  styleUrls: ['./tour-card.css']
})
export class TourCardComponent {
  @Input() tour!: TourCard;
  @Input() showRemoveButton = false;
  @Input() showViewButton = true;
  @Input() showEditButton = false
  @Input() showDeleteButton = false;
  @Input() showSaveButton = true;
  @Input() isSaved = false;
  @Output() view = new EventEmitter<number>();
  @Output() remove = new EventEmitter<number>();
  @Output() save = new EventEmitter<number>();
  @Output() removeSaved = new EventEmitter<number>();
  @Output() delete = new EventEmitter<number>();
  @Input() mode: 'view' | 'manage' = 'view';
  
  constructor(
    private router: Router
  ){}


  readonly fallbackImage =
    'https://images.unsplash.com/photo-1583531352515-8884af319dc1?auto=format&fit=crop&w=900&q=80';

  /*get locationLabel(): string {
    return [this.tour.city, this.tour.country].filter(Boolean).join(', ');
  }*/
get locationLabel(): string {
  // Prioridad: city + country
  if (this.tour.city && this.tour.country) {
    return `${this.tour.city}, ${this.tour.country}`;
  }
  // Si tiene city sola
  if (this.tour.city) {
    return this.tour.city;
  }
  // Si tiene country sola
  if (this.tour.country) {
    return this.tour.country;
  }
  // Si tiene location (string completo)
  if (this.tour.location) {
    return this.tour.location;
  }
  return 'Ubicación por confirmar';
}
getEnvironmentLabel(): string {
  const labels: Record<string, string> = {
    'EXTERIOR': 'Exterior',
    'INTERIOR': 'Interior',
    'MIXED': 'Mixto'
  };
  return labels[this.tour.environment] || this.tour.environment || '';
}
  get visibleCategories(): string[] {
    return this.tour.categories?.filter(Boolean).slice(0, 2) ?? [];
  }

  onView(): void {
    this.view.emit(this.tour.id);
  }

  onRemove(): void {
    this.remove.emit(this.tour.id);
  }

  onSave(): void {
    this.save.emit(this.tour.id);
  }

  onRemoveSaved(): void {
    this.removeSaved.emit(this.tour.id);
  }

  onToggleSaved(): void {
    if (this.isSaved) {
      this.onRemoveSaved();
      return;
    }
    this.onSave();
  }

  formatPrice(price: number): string {
    return new Intl.NumberFormat('es-CO', {
      style: 'currency',
      currency: 'COP',
      minimumFractionDigits: 0
    }).format(price);
  }

  useFallbackImage(event: Event): void {
    const image = event.target as HTMLImageElement;
    if (image.src !== this.fallbackImage) {
      image.src = this.fallbackImage;
    }
  }

  goToEdit(): void {
    this.router.navigate(['/admin/recorridos', this.tour.id, 'editar']);
  }
  onDelete(): void {
    console.log('🔴 TourCard: Emitiendo delete para tour ID:', this.tour.id);
    this.delete.emit(this.tour.id);
  }
}
