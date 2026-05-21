import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Tour, TourCard } from '../models/tour-model';

@Injectable({ providedIn: 'root' })
export class TourService {

  constructor(private readonly http: HttpClient) {}

  /**
   * Obtener todos los recorridos
   * GET /api/tours
   */
  getAllTours(): Observable<Tour[]> {
    return this.http.get<Tour[]>(`/api/tours`);
  }

  /**
   * Obtener un recorrido por ID
   * GET /api/tours/{tourId}
   */
  getTourById(tourId: number): Observable<Tour> {
    return this.http.get<Tour>(`/api/tours/${tourId}`);
  }

  /**
   * Obtener recorridos en formato simplificado para tarjetas
   */
  /*getToursAsCards(): Observable<TourCard[]> {
    return this.getAllTours().pipe(
         
         console.log('🟢 Cards generadas:', cards);
      map(tours => tours.map(tour => this.mapToCard(tour)))
    );
  }*/

  getToursAsCards(): Observable<TourCard[]> {
  return this.getAllTours().pipe(
    map(tours => {
      console.log('🔵 Tours recibidos del backend:', tours);
      const cards = tours.map(tour => this.mapToCard(tour));
      console.log('🟢 Cards generadas:', cards);
      return cards;
    })
  );
}

  /**
   * Mapea Tour a TourCard
   */
  private mapToCard(tour: Tour): TourCard {
    // Extraer ciudad de la ubicación (ej: "Medellín, Colombia")
    const locationParts = tour.location?.split(',') || [];
    const city = locationParts[0]?.trim() || '';
    const country = locationParts[1]?.trim() || 'Colombia';
    
    // Obtener primera imagen del álbum
    const imageUrl = tour.album?.images?.[0]?.url || 'assets/images/default-tour.jpg';
    
    // Convertir categorías a array de strings
    const categories = tour.categories.map(cat => 
      typeof cat === 'string' ? cat : cat.name
    );

    return {
      id: tour.id,
      name: tour.name,
      city: city,
      country: country,
      categories: categories,
      environment: tour.environment,
      price: tour.price,
      imageUrl: imageUrl
    };
  }
}