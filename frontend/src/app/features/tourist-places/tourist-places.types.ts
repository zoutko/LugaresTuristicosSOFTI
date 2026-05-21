export type TouristPlaceEnvironment = 'INTERIOR' | 'MIXED' | 'EXTERIOR';

export interface TouristPlaceLocation {
  city?: string | null;
  department?: string | null;
  country?: string | null;
  latitude?: number | null;
  longitude?: number | null;
}

export interface TouristPlaceActivity {
  id: number;
  description: string;
}

export interface TouristPlace {
  id: number;
  name: string;
  description?: string | null;
  duration?: string | null;
  environment?: TouristPlaceEnvironment | null;
  location?: TouristPlaceLocation | null;
  activities?: TouristPlaceActivity[];
  categories?: string[];
  totalPhotos?: number;
}
