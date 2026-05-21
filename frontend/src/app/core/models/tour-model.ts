// src/app/core/models/tour-model.ts

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
}

export interface Album {
  id?: number;
  title?: string;
  images?: AlbumImage[];
}

export interface Tour {
  id: number;
  name: string;
  categories: Category[] | string[];
  environment: string;
  description: string;
  recommendations: string;
  price: number;
  location: string;
  meetingPoint: string;
  itinerary: any[];
  tourOffer: any;
  album: Album;
}

export interface TourCard {
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
}