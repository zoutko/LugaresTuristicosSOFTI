import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Tour, TourCard } from '../models/tour-model';

@Injectable({ providedIn: 'root' })
export class TourService {
  constructor(private readonly http: HttpClient) {}

  getAllTours(): Observable<Tour[]> {
    return this.http.get<Tour[]>('/api/tours');
  }

  getTourById(tourId: number): Observable<Tour> {
    return this.http.get<Tour>(`/api/tours/${tourId}`);
  }

  getToursAsCards(): Observable<TourCard[]> {
    return this.getAllTours().pipe(
      map((tours) => (tours ?? []).map((tour) => this.mapToCard(tour)))
    );
  }

  private mapToCard(tour: Tour): TourCard {
    const locationParts = tour.location?.split(',') || [];
    const city = locationParts[0]?.trim() || '';
    const country = locationParts[1]?.trim() || 'Colombia';

    const imageUrl =
      tour.album?.photos?.[0]?.filePath ||
      tour.album?.currentPhoto?.filePath ||
      tour.album?.images?.[0]?.url ||
      tour.album?.images?.[0]?.filePath ||
      '';

    const categories = (tour.categories ?? []).map((cat) =>
      typeof cat === 'string' ? cat : cat.name
    );

    return {
      id: tour.id,
      name: tour.name,
      city,
      country,
      categories,
      environment: tour.environment,
      price: tour.price,
      imageUrl,
    };
  }
}
