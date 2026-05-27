import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { TourCard } from '../../core/models/tour-model';

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
  @Input() isSaved = false;
  @Output() view = new EventEmitter<number>();
  @Output() remove = new EventEmitter<number>();
  @Output() save = new EventEmitter<number>();
  @Output() removeSaved = new EventEmitter<number>();
  

  readonly fallbackImage =
    'https://images.unsplash.com/photo-1583531352515-8884af319dc1?auto=format&fit=crop&w=900&q=80';

  get locationLabel(): string {
    return [this.tour.city, this.tour.country].filter(Boolean).join(', ');
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
}
