import { CommonModule } from '@angular/common';
import { Component, DestroyRef, computed, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { catchError, forkJoin, map, of, switchMap } from 'rxjs';

import { TourService } from '../../../core/services/tour.service';
import { SavedToursService } from '../../../core/services/saved-tours-service';
import { TouristPlaceApiService } from '../../../core/services/tourist-place-api.service';
import { TourReviewService } from '../../../core/services/tour-review.service';
import { ItineraryItem, Tour } from '../../../core/models/tour-model';
import { TourReview } from '../../../core/models/review-model';
import { ToastComponent, ToastVariant } from '../../../shared/toast/toast';

@Component({
  selector: 'app-tour-detail',
  standalone: true,
  imports: [CommonModule, RouterLink, ToastComponent],
  templateUrl: './tour-detail.html',
  styleUrl: './tour-detail.css',
})
export class TourDetailComponent {
  private readonly tourApi = inject(TourService);
  private readonly savedToursApi = inject(SavedToursService);
  private readonly placeApi = inject(TouristPlaceApiService);
  private readonly reviewApi = inject(TourReviewService);
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly destroyRef = inject(DestroyRef);

  readonly tour = signal<Tour | null>(null);
  readonly loading = signal(true);
  readonly error = signal('');

  readonly isSaved = signal(false);
  readonly saving = signal(false);

  readonly bookingDate = signal<string>('');
  readonly qtyAdults = signal<number>(0);
  readonly qtyChildren = signal<number>(0);
  readonly qtyStudents = signal<number>(0);

  readonly itineraryPlaceNames = signal<Record<number, string>>({});

  readonly reviews = signal<TourReview[]>([]);
  readonly reviewsLoading = signal(false);
  readonly reviewsError = signal('');
  readonly reviewComment = signal('');
  readonly reviewRating = signal<number>(0);
  readonly reviewSubmitting = signal(false);
  readonly editingReviewId = signal<number | null>(null);

  toastOpen = false;
  toastMessage = '';
  toastVariant: ToastVariant = 'info';

  readonly categories = computed(() => (this.tour()?.categories ?? []).filter(Boolean));
  readonly itinerary = computed<ItineraryItem[]>(() => {
    const items = (this.tour()?.itinerary ?? []) as ItineraryItem[];
    return [...items].sort((a, b) => (a.position ?? 0) - (b.position ?? 0));
  });

  private readonly fallbackImage =
    'https://images.unsplash.com/photo-1583531352515-8884af319dc1?auto=format&fit=crop&w=1400&q=80';

  readonly heroImage = computed(() => {
    const album = this.tour()?.album;
    return album?.photos?.[0]?.filePath || album?.currentPhoto?.filePath || this.fallbackImage;
  });

  readonly adultPrice = computed(() => this.tour()?.price ?? 0);
  readonly childrenPrice = computed(() => Math.round((this.adultPrice() * 0.75) / 100) * 100);
  readonly studentPrice = computed(() => Math.round((this.adultPrice() * 0.5) / 100) * 100);

  readonly totalPrice = computed(() => {
    return (
      this.qtyAdults() * this.adultPrice() +
      this.qtyChildren() * this.childrenPrice() +
      this.qtyStudents() * this.studentPrice()
    );
  });

  constructor() {
    this.route.paramMap
      .pipe(
        map((params) => Number(params.get('id'))),
        switchMap((id) => {
          this.loading.set(true);
          this.error.set('');
          this.tour.set(null);
          this.isSaved.set(false);
          this.cancelEditReview();

          if (!Number.isFinite(id) || id <= 0) {
            return of({ tour: null as Tour | null, invalid: true });
          }

          return this.tourApi.getTourById(id).pipe(
            map((tour) => ({ tour, invalid: false })),
            catchError(() => of({ tour: null as Tour | null, invalid: false }))
          );
        }),
        takeUntilDestroyed(this.destroyRef)
      )
      .subscribe(({ tour, invalid }) => {
        if (invalid) {
          this.error.set('El recorrido solicitado no es válido.');
          this.loading.set(false);
          return;
        }

        if (!tour) {
          this.error.set('No fue posible cargar el detalle del recorrido.');
          this.loading.set(false);
          return;
        }

        this.tour.set(tour);
        this.loading.set(false);
        this.refreshSavedState();
        this.loadItineraryPlaceNames();
        this.loadReviews();
      });
  }

  itineraryPlaceName(item: ItineraryItem): string {
    const fromMap = this.itineraryPlaceNames()[item.touristPlaceId];
    const fromDto = item.touristPlaceName ?? '';
    return (fromDto || fromMap || `Lugar ${item.touristPlaceId}`).trim();
  }

  private loadItineraryPlaceNames(): void {
    const items = this.tour()?.itinerary ?? [];
    if (!items || items.length === 0) return;

    const existing = this.itineraryPlaceNames();
    const missingIds = Array.from(
      new Set(
        items
          .filter((item) => !item.touristPlaceName)
          .map((item) => item.touristPlaceId)
          .filter((id) => Number.isFinite(id) && id > 0 && !existing[id])
      )
    );

    if (missingIds.length === 0) return;

    forkJoin(
      missingIds.map((id) =>
        this.placeApi.getPlace(id).pipe(
          map((place) => ({ id, name: place?.name ?? '' })),
          catchError(() => of({ id, name: '' }))
        )
      )
    )
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((results) => {
        const next: Record<number, string> = { ...this.itineraryPlaceNames() };
        results.forEach((r) => {
          if (r?.id && r.name) {
            next[r.id] = r.name;
          }
        });
        this.itineraryPlaceNames.set(next);
      });
  }

  formatPrice(price: number): string {
    return new Intl.NumberFormat('es-CO', {
      style: 'currency',
      currency: 'COP',
      minimumFractionDigits: 0,
      maximumFractionDigits: 0,
    }).format(price);
  }

  getLocationLabel(): string {
    return this.tour()?.location || 'Ubicación por confirmar';
  }

  getMeetingPointLabel(): string {
    return this.tour()?.meetingPoint || 'Punto de encuentro por confirmar';
  }

  getRecommendations(): string[] {
    const raw = (this.tour()?.recommendations ?? '').trim();
    if (!raw) return [];

    return raw
      .split(/\r?\n/)
      .map((line) => line.replace(/^[-•\s]+/, '').trim())
      .filter(Boolean);
  }

  private getUserId(): number | null {
    const raw = localStorage.getItem('auth.userId');
    const parsed = raw ? Number(raw) : NaN;
    return Number.isFinite(parsed) && parsed > 0 ? parsed : null;
  }

  isLoggedIn(): boolean {
    return Boolean(localStorage.getItem('auth.token'));
  }

  canPublishReview(): boolean {
    return this.isLoggedIn() && Boolean(this.getUserId());
  }

  private refreshSavedState(): void {
    const userId = this.getUserId();
    const tourId = this.tour()?.id;

    if (!userId || !tourId) return;

    this.savedToursApi.getSavedTours(userId).subscribe({
      next: (tours) => {
        const found = (tours ?? []).some((t) => Number(t?.id) === Number(tourId));
        this.isSaved.set(found);
      },
      error: () => {
        // Non-blocking.
      },
    });
  }

  toggleSaved(): void {
    if (this.saving()) return;

    const userId = this.getUserId();
    const tourId = this.tour()?.id;

    if (!tourId) return;

    if (!userId) {
      this.router.navigate(['/auth'], { queryParams: { returnUrl: this.router.url } });
      return;
    }

    this.saving.set(true);

    const request = this.isSaved()
      ? this.savedToursApi.removeSavedTour(userId, tourId)
      : this.savedToursApi.saveTour(userId, tourId);

    request.subscribe({
      next: () => {
        this.saving.set(false);
        this.isSaved.set(!this.isSaved());
        this.showToast(this.isSaved() ? 'Recorrido guardado.' : 'Recorrido eliminado de guardados.', 'success');
      },
      error: () => {
        this.saving.set(false);
        this.showToast('No fue posible actualizar tus guardados.', 'error');
      },
    });
  }

  setRating(rating: number): void {
    if (!this.canPublishReview()) return;
    const next = Math.max(1, Math.min(5, rating));
    this.reviewRating.set(next);
  }

  loadReviews(): void {
    const tourId = this.tour()?.id;
    if (!tourId) return;

    this.reviewsLoading.set(true);
    this.reviewsError.set('');

    this.reviewApi
      .getReviews(tourId)
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (reviews) => {
          const sorted = [...(reviews ?? [])].sort((a, b) => {
            const da = a.publicationDate ?? '';
            const db = b.publicationDate ?? '';
            return db.localeCompare(da);
          });
          this.reviews.set(sorted);
          this.reviewsLoading.set(false);
        },
        error: () => {
          this.reviewsLoading.set(false);
          this.reviewsError.set('No fue posible cargar las reseñas.');
        },
      });
  }

  submitReview(): void {
    if (this.reviewSubmitting()) return;

    const tourId = this.tour()?.id;
    const userId = this.getUserId();
    const editingReviewId = this.editingReviewId();

    if (!tourId) return;
    if (!userId || !this.isLoggedIn()) {
      this.router.navigate(['/auth'], { queryParams: { returnUrl: this.router.url } });
      return;
    }

    const rating = this.reviewRating();
    const comment = this.reviewComment().trim();

    if (rating < 1 || rating > 5) {
      this.showToast('Selecciona una calificación (1 a 5).', 'error');
      return;
    }

    if (!comment) {
      this.showToast('Escribe un comentario para tu reseña.', 'error');
      return;
    }

    this.reviewSubmitting.set(true);

    const request = editingReviewId
      ? this.reviewApi.updateReview({
          tourId,
          reviewId: editingReviewId,
          requesterId: userId,
          rating,
          comment,
        })
      : this.reviewApi.createReview({ tourId, authorId: userId, rating, comment });

    request
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: (review) => {
          this.reviewSubmitting.set(false);
          this.reviewComment.set('');
          this.reviewRating.set(0);
          this.editingReviewId.set(null);
          this.reviews.set(
            editingReviewId
              ? this.reviews().map((item) => (item.id === review.id ? review : item))
              : [review, ...this.reviews()]
          );
          this.showToast(
            editingReviewId ? 'Reseña actualizada.' : 'Reseña publicada.',
            'success'
          );
        },
        error: () => {
          this.reviewSubmitting.set(false);
          this.showToast(
            editingReviewId ? 'No fue posible actualizar la reseña.' : 'No fue posible publicar la reseña.',
            'error'
          );
        },
      });
  }

  canEditReview(review: TourReview): boolean {
    const userId = this.getUserId();
    return Boolean(userId && review?.authorId === userId);
  }

  canDeleteReview(review: TourReview): boolean {
    const userId = this.getUserId();
    return Boolean(userId && review?.authorId === userId);
  }

  startEditReview(review: TourReview): void {
    if (!this.canEditReview(review) || this.reviewSubmitting()) return;

    this.editingReviewId.set(review.id);
    this.reviewRating.set(review.rating);
    this.reviewComment.set(review.comment ?? '');
  }

  cancelEditReview(): void {
    if (this.reviewSubmitting()) return;

    this.editingReviewId.set(null);
    this.reviewRating.set(0);
    this.reviewComment.set('');
  }

  deleteReview(reviewId: number): void {
    const tourId = this.tour()?.id;
    const userId = this.getUserId();

    if (!tourId || !userId || !reviewId) return;

    this.reviewApi
      .deleteReview({ tourId, reviewId, requesterId: userId })
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe({
        next: () => {
          this.reviews.set(this.reviews().filter((r) => r.id !== reviewId));
          if (this.editingReviewId() === reviewId) {
            this.cancelEditReview();
          }
          this.showToast('Reseña eliminada.', 'success');
        },
        error: () => {
          this.showToast('No fue posible eliminar la reseña.', 'error');
        },
      });
  }

  trackByReviewId(_index: number, review: TourReview): number {
    return review.id;
  }

  changeQty(type: 'adults' | 'children' | 'students', delta: number): void {
    const clamp = (value: number) => Math.max(0, Math.min(99, value));

    if (type === 'adults') this.qtyAdults.set(clamp(this.qtyAdults() + delta));
    if (type === 'children') this.qtyChildren.set(clamp(this.qtyChildren() + delta));
    if (type === 'students') this.qtyStudents.set(clamp(this.qtyStudents() + delta));
  }

  reserve(): void {
    this.showToast('La reserva aún no está disponible.', 'info');
  }

  private showToast(message: string, variant: ToastVariant = 'info'): void {
    this.toastMessage = message;
    this.toastVariant = variant;
    this.toastOpen = true;
  }

  closeToast(): void {
    this.toastOpen = false;
  }
}
