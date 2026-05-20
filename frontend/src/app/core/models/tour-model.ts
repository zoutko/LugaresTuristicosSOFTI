export interface TourResponse {
  id: number;
  name: string;                    // Nombre del tour
  categories: string[];            // Categorías/etiquetas
  environment: string;             // Tipo de ambiente
  description: string;             // Descripción
  recommendations: string;         // Recomendaciones
  price: number;                   // Precio
  location: string;                // Ubicación completa
  meetingPoint: string;            // Punto de encuentro
  itinerary: ItineraryItemResponse[];  // Itinerario
  tourOffer: TourOfferResponse;    // Oferta del tour
  album: AlbumResponse;            // Álbum de imágenes
}

export interface ItineraryItemResponse {
  id?: number;
  title?: string;
  description?: string;
  duration?: string;
}

export interface TourOfferResponse {
  id?: number;
  discount?: number;
  validUntil?: string;
}

export interface AlbumResponse {
  id?: number;
  title?: string;
  images?: string[];
}

// Para la tarjeta, usamos una versión simplificada
export interface SavedTourCard {
  id: number;
  name: string;
  location: string;
  categories: string[];
  price: number;
  imageUrl?: string;  // Primera imagen del album si existe
}