// src/app/core/models/tour-model.ts

export interface TourTag {
  id?: number;
  name: string;
  color?: string;
}

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