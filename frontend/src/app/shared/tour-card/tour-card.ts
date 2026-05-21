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
  @Output() view = new EventEmitter<number>();
  @Output() remove = new EventEmitter<number>();

  onView(): void {
    this.view.emit(this.tour.id);
  }

  onRemove(): void {
    this.remove.emit(this.tour.id);
  }

  formatPrice(price: number): string {
    return new Intl.NumberFormat('es-CO', {
      style: 'currency',
      currency: 'COP',
      minimumFractionDigits: 0
    }).format(price);
  }
}