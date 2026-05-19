import { TouristPlace } from './tourist-places.types';

export const TOURIST_PLACES_MOCK: TouristPlace[] = [
  {
    id: 'b0f9b3a9-9d5c-4c3d-ae61-7d7b92a5b2a1',
    name: 'Parque Natural La Cascada',
    description: 'Senderos, miradores y zona de picnic. Ideal para una salida corta en familia.',
    duration: '3 horas',
    environment: 'EXTERIOR',
    location: { city: 'Medellín', department: 'Antioquia', country: 'Colombia' },
    album: {
      photos: [
        {
          filePath:
            'https://images.unsplash.com/photo-1441974231531-c6227db76b6e?auto=format&fit=crop&w=1200&q=80',
          description: 'Bosque y sendero',
        },
      ],
    },
    categories: [
      { id: 1, name: 'Naturaleza' },
      { id: 2, name: 'Senderismo' },
    ],
  },
  {
    id: 'a2d4e7a1-1df7-4e2b-b9ef-2e5c87b66f12',
    name: 'Museo de Historia Local',
    description: 'Exhibiciones permanentes, visitas guiadas y muestras temporales.',
    duration: '1.5 horas',
    environment: 'INTERIOR',
    location: { city: 'Bogotá', department: 'Cundinamarca', country: 'Colombia' },
    album: {
      photos: [
        {
          filePath:
            'https://tse2.mm.bing.net/th/id/OIP.RFSRAYjQ1r9w-avSjfVmkQHaE8?rs=1&pid=ImgDetMain&o=7&rm=3',
          description: 'Sala principal',
        },
      ],
    },
    categories: [
      { id: 3, name: 'Cultural' },
      { id: 4, name: 'Museos' },
    ],
  },
  {
    id: 'd6e1b8ae-2d0b-4e7d-9d89-4db0a4e29f6c',
    name: 'Mirador del Valle',
    description: 'Vista panorámica al atardecer. Hay cafeterías y artesanías alrededor.',
    duration: '2 horas',
    environment: 'EXTERIOR',
    location: { city: 'Cali', department: 'Valle del Cauca', country: 'Colombia' },
    album: {
      photos: [
        {
          filePath:
            'https://images.unsplash.com/photo-1501785888041-af3ef285b470?auto=format&fit=crop&w=1200&q=80',
          description: 'Mirador al atardecer',
        },
      ],
    },
    categories: [
      { id: 5, name: 'Paisajes' },
      { id: 6, name: 'Fotografía' },
    ],
  },
  {
    id: '8a9f0f12-78ea-4d64-9b23-6f1f7f9b0aa3',
    name: 'Ruta Gastronómica Central',
    description: 'Recorrido por restaurantes locales con degustaciones y platos típicos.',
    duration: '4 horas',
    environment: 'MIXED',
    location: { city: 'Barranquilla', department: 'Atlántico', country: 'Colombia' },
    album: {
      photos: [
        {
          filePath:
            'https://images.unsplash.com/photo-1529692236671-f1f6cf9683ba?auto=format&fit=crop&w=1200&q=80',
          description: 'Platos típicos',
        },
      ],
    },
    categories: [
      { id: 7, name: 'Gastronomía' },
      { id: 8, name: 'Tours' },
    ],
  },
  {
    id: 'f4c2a9a5-5a1a-4d0c-9f24-8a2f3a6c1a10',
    name: 'Centro Artesanal del Río',
    description: 'Mercado de artesanías con talleres cortos y música en vivo los fines de semana.',
    duration: '2.5 horas',
    environment: 'MIXED',
    location: { city: 'Cartagena', department: 'Bolívar', country: 'Colombia' },
    album: {
      photos: [
        {
          filePath:
            'https://images.unsplash.com/photo-1520975916090-3105956dac38?auto=format&fit=crop&w=1200&q=80',
          description: 'Artesanías',
        },
      ],
    },
    categories: [
      { id: 9, name: 'Cultural' },
      { id: 10, name: 'Artesanías' },
    ],
  },
  {
    id: '21c2c4f7-7e28-46a1-a7d5-6ae1f9d7a8f2',
    name: 'Sendero del Bosque Nublado',
    description: 'Caminata de dificultad media con guías locales. Llevar impermeable.',
    duration: '5 horas',
    environment: 'EXTERIOR',
    location: { city: 'Manizales', department: 'Caldas', country: 'Colombia' },
    album: {
      photos: [
        {
          filePath:
            'https://images.unsplash.com/photo-1469474968028-56623f02e42e?auto=format&fit=crop&w=1200&q=80',
          description: 'Bosque nublado',
        },
      ],
    },
    categories: [
      { id: 1, name: 'Naturaleza' },
      { id: 2, name: 'Senderismo' },
    ],
  },
];
