import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SavedTour } from '../models/tour-model';

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
}