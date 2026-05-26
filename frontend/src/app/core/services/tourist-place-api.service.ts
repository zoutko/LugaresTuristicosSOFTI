import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { from, Observable, of } from 'rxjs';
import { catchError, map, mergeMap } from 'rxjs/operators';
import { TouristPlace, TouristPlaceAlbum } from '../../features/tourist-places/tourist-places.types';

@Injectable({ providedIn: 'root' })
export class TouristPlaceApiService {
  private readonly http = inject(HttpClient);

  getPlaces(): Observable<TouristPlace[]> {
    return this.http.get<TouristPlace[]>('/api/places');
  }

  getPlace(placeId: number): Observable<TouristPlace> {
    return this.http.get<TouristPlace>(`/api/places/${placeId}`);
  }

  getAlbum(placeId: number): Observable<TouristPlaceAlbum> {
    return this.http.get<TouristPlaceAlbum>(`/api/places/${placeId}/media/album`);
  }

  /**
   * Emits best-effort cover urls per place (skips failed albums).
   */
  coverPhotos$(places: TouristPlace[], concurrency = 6): Observable<{ placeId: number; coverUrl: string }> {
    return from(places).pipe(
      mergeMap(
        (place) =>
          this.getAlbum(place.id).pipe(
            map((album) => ({
              placeId: place.id,
              coverUrl: album.photos?.[0]?.filePath ?? album.currentPhoto?.filePath ?? '',
            })),
            catchError(() => of({ placeId: place.id, coverUrl: '' }))
          ),
        concurrency
      )
    );
  }

  deletePlace(placeId: number, headers: HttpHeaders): Observable<void> {
    return this.http.delete<void>(`/api/places/${placeId}`, { headers });
  }
}
