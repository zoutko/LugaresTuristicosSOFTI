import {
  filterTouristPlaces,
  getTouristPlaceCategories,
  getTouristPlaceEnvironmentLabel,
  getTouristPlaceLocation,
} from './tourist-place.utils';

import { TouristPlace } from '../../features/tourist-places/tourist-places.types';

describe('tourist-place.utils', () => {
  const places: TouristPlace[] = [
    {
      id: 1,
      name: 'Cascada Azul',
      environment: 'EXTERIOR',
      categories: ['Natural', 'Aventura'],
      location: { city: 'Medellín', country: 'Colombia' },
    },
    {
      id: 2,
      name: 'Museo Histórico',
      environment: 'INTERIOR',
      categories: ['Cultural'],
      location: { city: 'Bogotá', country: 'Colombia' },
    },
  ];

  it('filterTouristPlaces filters by term and environment', () => {
    const result = filterTouristPlaces({
      places,
      term: 'museo',
      environment: 'INTERIOR',
    });

    expect(result.map((p) => p.id)).toEqual([2]);
  });

  it('filterTouristPlaces returns all when environment is ALL', () => {
    const result = filterTouristPlaces({
      places,
      term: '',
      environment: 'ALL',
    });

    expect(result.length).toBe(2);
  });

  it('getTouristPlaceLocation uses city + country when present, otherwise fallback', () => {
    expect(getTouristPlaceLocation(places[0])).toBe('Medellín, Colombia');

    const withoutLocation: TouristPlace = { id: 3, name: 'X' };
    expect(getTouristPlaceLocation(withoutLocation)).toBe('Ubicacion por confirmar');
  });

  it('getTouristPlaceEnvironmentLabel returns label or default', () => {
    expect(getTouristPlaceEnvironmentLabel('EXTERIOR')).toBe('Exterior');
    expect(getTouristPlaceEnvironmentLabel(undefined)).toBe('Ambiente');
  });

  it('getTouristPlaceCategories returns up to 3 categories or default', () => {
    const many: TouristPlace = { id: 9, name: 'Y', categories: ['A', 'B', 'C', 'D'] };
    expect(getTouristPlaceCategories(many)).toEqual(['A', 'B', 'C']);

    const none: TouristPlace = { id: 10, name: 'Z', categories: [] };
    expect(getTouristPlaceCategories(none)).toEqual(['Cultural']);
  });
});
