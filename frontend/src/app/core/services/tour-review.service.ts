import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { TourReview } from '../models/review-model';
import { AuthTokenService } from './auth-token.service';

@Injectable({ providedIn: 'root' })
export class TourReviewService {
  constructor(
    private readonly http: HttpClient,
    private readonly authToken: AuthTokenService
  ) {}

  getReviews(tourId: number): Observable<TourReview[]> {
    return this.http.get<TourReview[]>(`/api/tours/${tourId}/reviews`, {
      headers: this.authToken.getAuthHeaders(),
    });
  }

  createReview(params: { tourId: number; authorId: number; rating: number; comment: string }): Observable<TourReview> {
    const httpParams = new HttpParams().set('authorId', String(params.authorId));

    return this.http.post<TourReview>(
      `/api/tours/${params.tourId}/reviews`,
      {
        rating: params.rating,
        comment: params.comment,
      },
      {
        params: httpParams,
        headers: this.authToken.getAuthHeaders(),
      }
    );
  }

  deleteReview(params: { tourId: number; reviewId: number; requesterId: number }): Observable<void> {
    return this.http.delete<void>(
      `/api/tours/${params.tourId}/reviews/${params.reviewId}/user/${params.requesterId}`,
      {
        headers: this.authToken.getAuthHeaders(),
      }
    );
  }
}
