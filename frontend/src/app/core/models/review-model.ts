export interface TourReview {
  id: number;
  authorId: number;
  authorName: string;
  tourId: number;
  rating: number;
  publicationDate: string; // LocalDate from backend (YYYY-MM-DD)
  comment: string;
}
