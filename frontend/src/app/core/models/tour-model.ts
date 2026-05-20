export interface TourTag {
  name: string;
  color?: string;
}

export interface SavedTour {
  id:number
  ciudad: string;
  titulo: string;
  descripcion?: string;
  etiquetas: string[] | TourTag[];
  precio: number;
  imagen?: string;
}

export interface SavedToursResponse {
  userId: number;
  tours: SavedTour[];
  total: number;
}