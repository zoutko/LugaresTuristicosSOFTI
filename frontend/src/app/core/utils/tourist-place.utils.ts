import { TOURIST_PLACE_ENVIRONMENT_LABELS, TouristPlaceEnvironment } from '../constants/tourist-place.constants';
import { TouristPlace } from '../../features/tourist-places/tourist-places.types';

export function filterTouristPlaces(params: {
  places: TouristPlace[];
  term: string;
  environment: TouristPlaceEnvironment;
}): TouristPlace[] {
  const normalizedTerm = params.term.trim().toLowerCase();

  return params.places.filter((place) => {
    const city = place.location?.city ?? '';
    const categoryNames = place.categories?.join(' ') ?? '';
    const matchesTerm = `${place.name} ${city} ${categoryNames}`
      .toLowerCase()
      .includes(normalizedTerm);

    const matchesEnvironment =
      params.environment === 'ALL' || place.environment === params.environment;

    return matchesTerm && matchesEnvironment;
  });
}

export function getTouristPlaceLocation(place: TouristPlace): string {
  const city = place.location?.city;
  const country = place.location?.country;

  if (city && country) {
    return `${city}, ${country}`;
  }

  return city || country || 'Ubicacion por confirmar';
}

export function getTouristPlaceEnvironmentLabel(environment?: TouristPlace['environment']): string {
  return environment ? TOURIST_PLACE_ENVIRONMENT_LABELS[environment] : 'Ambiente';
}

export function getTouristPlaceCategories(place: TouristPlace): string[] {
  const categories = place.categories?.filter(Boolean) ?? [];
  return categories.length > 0 ? categories.slice(0, 3) : ['Cultural'];
}
