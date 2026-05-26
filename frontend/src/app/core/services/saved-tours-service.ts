import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SavedTour, TourCard } from '../models/tour-model';
import { map } from 'rxjs/operators';
import { AuthTokenService } from './auth-token.service';

@Injectable({ providedIn: 'root' })
export class SavedToursService {

  constructor(private readonly http: HttpClient, private readonly authToken: AuthTokenService) {}

  getSavedTours(userId: number): Observable<SavedTour[]> {
    return this.http.get<SavedTour[]>(`/api/users/${userId}/saved-tours`, {
      headers: this.authToken.getAuthHeaders(),
    });
  }

  removeSavedTour(userId: number, tourId: number): Observable<void> {
    return this.http.delete<void>(`/api/users/${userId}/saved-tours/${tourId}`, {
      headers: this.authToken.getAuthHeaders(),
    });
  }

  saveTour(userId: number, tourId: number): Observable<void> {
    return this.http.post<void>(`/api/users/${userId}/saved-tours/${tourId}`, {}, {
      headers: this.authToken.getAuthHeaders(),
    });
  }

  getSavedToursAsCards(userId: number): Observable<TourCard[]> {
    return this.getSavedTours(userId).pipe(
      map((tours) => tours.map((tour) => this.mapToTourCard(tour)))
    );
  }

  private mapToTourCard(tour: SavedTour): TourCard {
    const imageUrl =
      tour.album?.photos?.[0]?.filePath ||
      tour.album?.currentPhoto?.filePath ||
      tour.imageUrl ||
      tour.imagen ||
      '';

    const city =
      tour.city ||
      tour.ciudad ||
      this.extractCityFromLocation(tour.location) ||
      '';

    return {
      id: tour.id,
      name: tour.name || '',
      city,
      country: tour.country || this.extractCountryFromLocation(tour.location) || 'Colombia',
      categories: tour.categories || [],
      environment: tour.environment || '',
      price: tour.price || 0,
      imageUrl,
    };
  }

  private extractCityFromLocation(location?: string | null): string {
    if (!location) return '';

    const [city] = location.split(',').map((part) => part.trim()).filter(Boolean);
    return city ?? '';
  }

  private extractCountryFromLocation(location?: string | null): string {
    if (!location) return '';

    const parts = location.split(',').map((part) => part.trim()).filter(Boolean);
    return parts.length > 1 ? parts[parts.length - 1] : '';
  }
}