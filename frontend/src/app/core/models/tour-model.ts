export interface TourTag {
  id?: number;
  name: string;
  color?: string;
}

export interface Category {
  id: number;
  name: string;
}

export interface AlbumImage {
  id?: number;
  url?: string;
  filePath?: string;
  fileName?: string;
  description?: string;
}

export interface Album {
  id?: number;
  title?: string;
  images?: AlbumImage[];
  photos?: AlbumImage[];
  currentPhoto?: AlbumImage | null;
}

export interface ItineraryItem {
  itineraryId: number;
  position: number;
  touristPlaceId: number;
  touristPlaceName?: string | null;
}

export interface Tour {
  id: number;
  name: string;
  categories: Category[] | string[];
  environment: string;
  description: string;
  recommendations: string;
  price: number;
  meetingPoint: string;
  meetingPointLocation?: {
    city?: string | null;
    department?: string | null;
    country?: string | null;
    latitude?: number | null;
    longitude?: number | null;
  } | null;
  itinerary: ItineraryItem[];
  tourOffer: any;
  album: Album;
  location: string; 
}

export interface TourCard {
  location?: string;
  id: number;
  name: string;
  city: string;
  country: string;
  categories: string[];
  environment: string;
  price: number;
  imageUrl: string;
}

export type SavedTourCard = TourCard;

export interface SavedTour {
  id: number;
  name?: string;
  city?: string;
  country?: string;
  ciudad?: string;
  titulo?: string;
  etiquetas: (string | TourTag)[];
  categories?: string[];
  precio: number;
  price?: number;
  imagen?: string;
  imageUrl?: string;
  duracion?: string;
  calificacion?: number;
  location?: string;
  description?: string;
  environment?: string;
  album?: Album | null;
}
