import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SavedTour } from '../models/tour-model';
import { TourCard } from '../models/tour-model';
import { map } from 'rxjs/operators';

@Injectable({ providedIn: 'root' })
export class SavedToursService {

  constructor(private readonly http: HttpClient) {}

  getSavedTours(userId: number): Observable<SavedTour[]> {
    return this.http.get<SavedTour[]>(`/api/users/${userId}/saved-tours`);
  }

  removeSavedTour(userId: number, tourId: number): Observable<void> {
    return this.http.delete<void>(`/api/users/${userId}/saved-tours/${tourId}`);
  }

  saveTour(userId: number, tourId: number): Observable<void> {
    return this.http.post<void>(`/api/users/${userId}/saved-tours/${tourId}`, {});
  }

getSavedToursAsCards(userId: number): Observable<TourCard[]> {
  return this.getSavedTours(userId).pipe(
    map(tours => tours.map(tour => this.mapToTourCard(tour)))
  );
}

private mapToTourCard(tour: any): TourCard {
  return {
    id: tour.id,
    name: tour.name || tour.titulo || '',
    city: tour.city || tour.ciudad?.split(',')[0] || '',
    country: tour.country || 'Colombia',
    categories: tour.categories || tour.etiquetas || [],
    environment: tour.environment || '',
    price: tour.price || tour.precio || 0,
    imageUrl: tour.imageUrl || tour.imagen || 'assets/images/default-tour.jpg'
  };
}
}