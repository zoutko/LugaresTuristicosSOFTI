export type TouristPlaceEnvironment = 'ALL' | 'INTERIOR' | 'MIXED' | 'EXTERIOR';

export const TOURIST_PLACE_ENVIRONMENTS = [
  { value: 'ALL', label: 'Todos' },
  { value: 'EXTERIOR', label: 'Exterior' },
  { value: 'INTERIOR', label: 'Interior' },
  { value: 'MIXED', label: 'Mixto' },
] as const;

export const TOURIST_PLACE_ENVIRONMENT_LABELS: Record<Exclude<TouristPlaceEnvironment, 'ALL'>, string> = {
  INTERIOR: 'Interior',
  MIXED: 'Mixto',
  EXTERIOR: 'Exterior',
};

export const TOURIST_PLACE_FALLBACK_IMAGES = [
  'https://images.unsplash.com/photo-1583531352515-8884af319dc1?auto=format&fit=crop&w=900&q=80',
  'https://images.unsplash.com/photo-1448375240586-882707db888b?auto=format&fit=crop&w=900&q=80',
  'https://images.unsplash.com/photo-1512813195386-6cf811ad3542?auto=format&fit=crop&w=900&q=80',
  'https://images.unsplash.com/photo-1564013799919-ab600027ffc6?auto=format&fit=crop&w=900&q=80',
] as const;
