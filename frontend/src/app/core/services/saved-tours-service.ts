import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, of } from 'rxjs';
import { SavedTour, SavedToursResponse } from '../models/tour-model';

@Injectable({ providedIn: 'root' })
export class SavedToursService {

  // Datos mock (valores por defecto)
  private mockTours: SavedTour[] = [
    {
      id: 1,
      ciudad: 'Medellín, Colombia',
      titulo: 'Descubre Medellín',
      etiquetas: ['Arte Urbano', 'Histórico'],
      precio: 90000,
      imagen: 'assets/images/medellin.jpg'
    },
    {
      id: 2,
      ciudad: 'Bogotá, Colombia',
      titulo: 'La Candelaria',
      etiquetas: ['Histórico', 'Cultural'],
      precio: 65000,
      imagen: 'assets/images/bogota.jpg'
    },
    {
      id: 3,
      ciudad: 'Cartagena, Colombia',
      titulo: 'Ciudad Amurallada',
      etiquetas: ['Cultural', 'Playas', 'Histórico'],
      precio: 120000,
      imagen: 'assets/images/cartagena.jpg'
    }
  ];

  constructor(private readonly http: HttpClient) {}

  /**
   * Obtener recorridos guardados por un usuario
   * GET /api/users/{userId}/saved-tours
   * 
   * TODO: Cuando el backend esté listo, descomentar:
   * return this.http.get<SavedToursResponse>(`/api/users/${userId}/saved-tours`);
   */
  getSavedTours(userId: number): Observable<SavedToursResponse> {
    // DATOS MOCK - Reemplazar con llamada real cuando el backend esté listo
    const response: SavedToursResponse = {
      userId: userId,
      tours: this.mockTours,
      total: this.mockTours.length
    };
    
    return of(response); 
  }

  /**
   * Eliminar un recorrido de la lista de guardados
   * DELETE /api/users/{userId}/saved-tours/{tourId}
   * 
   * TODO: Cuando el backend esté listo, descomentar:
   * return this.http.delete(`/api/users/${userId}/saved-tours/${tourId}`);
   */
  removeSavedTour(userId: number, tourId: number): Observable<void> {
    // DATOS MOCK - Simula eliminación
    this.mockTours = this.mockTours.filter(t => t.id !== tourId);
    return of(void 0);
  }

  /**
   * Verificar si un tour está guardado
   * GET /api/users/{userId}/saved-tours/{tourId}/exists
   */
  isTourSaved(userId: number, tourId: number): Observable<boolean> {
    const exists = this.mockTours.some(t => t.id === tourId);
    return of(exists);
  }
}