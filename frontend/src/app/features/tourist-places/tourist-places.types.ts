export interface Category {
  id: number;
  name: string;
}

export interface Photo {
  filePath: string;
  description?: string;
}

export type TouristPlaceEnvironment = 'INTERIOR' | 'MIXED' | 'EXTERIOR';

export interface TouristPlace {
  id: string;
  name: string;
  description?: string;
  duration?: string;
  environment?: TouristPlaceEnvironment;
  location?: {
    city?: string;
    department?: string;
    country?: string;
  };
  album?: {
    photos?: Photo[];
  };
  categories?: Category[];
}
