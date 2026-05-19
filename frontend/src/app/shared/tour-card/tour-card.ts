import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { SavedTour, TourTag } from '../../core/models/tour-model';

@Component({
  selector: 'app-tour-card',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './tour-card.html',
  styleUrls: ['./tour-card.css']
})
export class TourCardComponent {
  @Input() tour!: SavedTour;
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

  getTagName(tag: string | TourTag): string {
    if (typeof tag === 'string') {
      return tag;
    }
    return tag.name;
  }
}