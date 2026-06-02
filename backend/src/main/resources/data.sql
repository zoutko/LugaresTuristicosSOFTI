-- ============================================================
-- data.sql — src/main/resources/data.sql
-- IDs generados automáticamente por H2, FKs por subconsulta
-- ============================================================


-- ── CATEGORÍAS ───────────────────────────────────────────────
INSERT INTO categories (name) VALUES
('Historia'),
('Naturaleza'),
('Aventura'),
('Gastronomía'),
('Cultura'),
('Religión'),
('Arte');

-- ── USER TYPES ───────────────────────────────────────────────
INSERT INTO user_types (name, description) VALUES
('STUDENT', 'Descuento para estudiantes con carnet vigente'),
('SENIOR',  'Descuento para adultos mayores de 60 años'),
('CHILD',   'Descuento para niños menores de 12 años'),
('GENERAL', 'Tarifa general sin descuento especial');

-- ── USER PROFILES ────────────────────────────────────────────
INSERT INTO user_profiles (name, document, role_name) VALUES
('Administrador', '1053489671', 'ADMINISTRATOR'),
('Juan Perez',    '1234567890', 'USER'),
('Maria Lopez',   '9876543210', 'USER');

-- ── CONTACTS ─────────────────────────────────────────────────
INSERT INTO contacts (phone_number, user_profile_id) VALUES
('3123805426', (SELECT id FROM user_profiles WHERE document = '1053489671')),
('3001234567', (SELECT id FROM user_profiles WHERE document = '1234567890')),
('3009876543', (SELECT id FROM user_profiles WHERE document = '9876543210'));

-- ── USERS ────────────────────────────────────────────────────
INSERT INTO users (user_profile_id) VALUES
((SELECT id FROM user_profiles WHERE document = '1053489671')),
((SELECT id FROM user_profiles WHERE document = '1234567890')),
((SELECT id FROM user_profiles WHERE document = '9876543210'));

-- ── CREDENTIALS ──────────────────────────────────────────────
INSERT INTO credentials (email, password, state, role, user_id) VALUES
('admin@gmail.com', '$2a$10$49zGGo/7F3ItCvTAY22/veJCDQkncwZHDB3YqW9hvAFNFcNrTYJCC', 'ACTIVE', 'ADMINISTRATOR',
    (SELECT u.id FROM users u JOIN user_profiles p ON u.user_profile_id = p.id WHERE p.document = '1053489671')),
('user@gmail.com',  '$2a$10$lTux.l0yHzDZSlGzwRiUJedWM9u.HP1IlbWC287yLzcINWjnFDb0i', 'ACTIVE', 'USER',
    (SELECT u.id FROM users u JOIN user_profiles p ON u.user_profile_id = p.id WHERE p.document = '1234567890')),
('maria@gmail.com', '$2a$10$lTux.l0yHzDZSlGzwRiUJedWM9u.HP1IlbWC287yLzcINWjnFDb0i', 'ACTIVE', 'USER',
    (SELECT u.id FROM users u JOIN user_profiles p ON u.user_profile_id = p.id WHERE p.document = '9876543210'));

-- ── LUGARES TURÍSTICOS ───────────────────────────────────────
INSERT INTO places (name, description, duration, environment, city, department, country, latitude, longitude) VALUES
('Monserrate', 'Monserrate es uno de los sitios turisticos y religiosos mas representativos de Bogotá. Ubicado a mas de 3.100 metros sobre el nivel del mar', '3 horas', 'EXTERIOR', 'Bogotá', 'Cundinamarca', 'Colombia', 4.6072539, -74.0543090),
('Plaza de Bolivar', 'La Plaza de Bolivar es el corazon historico y politico de Colombia. Rodeada por edificios emblematicos como el Capitolio Nacional', '1 hora', 'EXTERIOR', 'Bogotá', 'Cundinamarca', 'Colombia', 4.5981, -74.0759),
('Museo del Oro', 'Museo con la mayor coleccion de piezas precolombinas en oro', '2 horas', 'INTERIOR', 'Bogotá', 'Cundinamarca', 'Colombia', 4.6017, -74.0721),
('Castillo de San Felipe', 'Fortaleza colonial del siglo XVII, Patrimonio de la Humanidad', '2 horas', 'MIXED', 'Cartagena', 'Bolivar', 'Colombia', 10.4236, -75.5380),
('Ciudad Amurallada', 'Centro historico de Cartagena rodeado de murallas coloniales', '3 horas', 'EXTERIOR', 'Cartagena', 'Bolivar', 'Colombia', 10.4227, -75.5497),
('Catedral de Sal de Zipaquira', 'Maravilla arquitectonica construida en el interior de una mina de sal', '3 horas', 'INTERIOR', 'Zipaquira', 'Cundinamarca', 'Colombia', 5.0276, -74.0093),
('Parque Tayrona', 'Parque natural con playas paradisiacas y senderos ecologicos', '6 horas', 'EXTERIOR', 'Santa Marta', 'Magdalena', 'Colombia', 11.3000, -73.9500),
('Villa de Leyva', 'Pueblo colonial reconocido por su arquitectura y calles empedradas', '4 horas', 'EXTERIOR', 'Villa de Leyva', 'Boyacá', 'Colombia', 5.6349, -73.5248),
('Guatape', 'Destino turistico famoso por la Piedra del Peñol y sus zocalos coloridos', '5 horas', 'EXTERIOR', 'Guatape', 'Antioquia', 'Colombia', 6.2342, -75.1635);

-- ── MEDIA (ALBUM + FOTOS) PARA LUGARES ───────────────────────
INSERT INTO albums (name, current_index) VALUES
('Monserrate', 0),
('Plaza de Bolivar', 0),
('Museo del Oro', 0),
('Castillo de San Felipe', 0),
('Ciudad Amurallada', 0),
('Catedral de Sal de Zipaquira', 0),
('Parque Tayrona', 0),
('Villa de Leyva', 0),
('Guatape', 0);

UPDATE places SET album_id = (SELECT id FROM albums WHERE name = 'Monserrate')             WHERE name = 'Monserrate';
UPDATE places SET album_id = (SELECT id FROM albums WHERE name = 'Plaza de Bolivar')       WHERE name = 'Plaza de Bolivar';
UPDATE places SET album_id = (SELECT id FROM albums WHERE name = 'Museo del Oro')          WHERE name = 'Museo del Oro';
UPDATE places SET album_id = (SELECT id FROM albums WHERE name = 'Castillo de San Felipe') WHERE name = 'Castillo de San Felipe';
UPDATE places SET album_id = (SELECT id FROM albums WHERE name = 'Ciudad Amurallada')      WHERE name = 'Ciudad Amurallada';
UPDATE places SET album_id = (SELECT id FROM albums WHERE name = 'Catedral de Sal de Zipaquira') WHERE name = 'Catedral de Sal de Zipaquira';
UPDATE places SET album_id = (SELECT id FROM albums WHERE name = 'Parque Tayrona') WHERE name = 'Parque Tayrona';
UPDATE places SET album_id = (SELECT id FROM albums WHERE name = 'Villa de Leyva') WHERE name = 'Villa de Leyva';
UPDATE places SET album_id = (SELECT id FROM albums WHERE name = 'Guatape') WHERE name = 'Guatape';

INSERT INTO photos (file_path, photo_description, album_id) VALUES
('https://radionacional-v3.s3.amazonaws.com/s3fs-public/node/article/field_image/MONSERRATE.jpg',
 'Vista panoramica desde Monserrate',
 (SELECT id FROM albums WHERE name = 'Monserrate')),
('https://tse1.mm.bing.net/th/id/OIP.eQHklS0cup4D_9xpmTP_pgHaLH?pid=ImgDet&w=474&h=711&rs=1&o=7&rm=3',
 'Teleférico de Monserrate',
 (SELECT id FROM albums WHERE name = 'Monserrate')),
('https://c2.staticflickr.com/8/7063/6864755356_ebeb16f9c7_b.jpg',
 'Vista general de la Plaza de Bolivar',
 (SELECT id FROM albums WHERE name = 'Plaza de Bolivar')),
('https://img.travesiasdigital.com/2019/03/pieza-museo-del-oro.jpg',
 'Fachada del Museo del Oro',
 (SELECT id FROM albums WHERE name = 'Museo del Oro')),
('https://tse1.mm.bing.net/th/id/OIP.BLuU4uHGGn9T1lKbh_eCpgHaE8?rs=1&pid=ImgDetMain&o=7&rm=3',
 'Murallas y calles coloniales',
 (SELECT id FROM albums WHERE name = 'Ciudad Amurallada')),
('https://i.ytimg.com/vi/sGVLNgJNs78/maxresdefault.jpg',
 'Fortaleza de San Felipe de Barajas',
 (SELECT id FROM albums WHERE name = 'Castillo de San Felipe')),
('https://estaticos-cdn.prensaiberica.es/clip/13dc1c52-63e6-4f52-8ef6-faac66adb64d_alta-aspect-ratio_default_0.jpg',
 'Interior de la Catedral de Sal',
 (SELECT id FROM albums WHERE name = 'Catedral de Sal de Zipaquira')),
('https://theorangebackpack.nl/wp-content/uploads/2023/03/Tayrona-4-scaled.jpg',
 'Playas del Parque Tayrona',
 (SELECT id FROM albums WHERE name = 'Parque Tayrona')),
('https://th.bing.com/th/id/R.fd735d6142171e7dfe7f0fa8fe405449?rik=0hu0rwfyFcf3mg&pid=ImgRaw&r=0',
 'Plaza principal de Villa de Leyva',
 (SELECT id FROM albums WHERE name = 'Villa de Leyva')),
('https://static1.thetravelimages.com/wordpress/wp-content/uploads/2022/10/Drone-shot-of-the-Rock-of-Guatape-also-called-La-Piedra-or-El-Pe%C3%B1ol.jpg',
 'Vista panoramica de Guatape',
 (SELECT id FROM albums WHERE name = 'Guatape'));

-- ── ACTIVIDADES ──────────────────────────────────────────────
INSERT INTO activities (description, place_id) VALUES
('Senderismo al cerro',                   (SELECT id FROM places WHERE name = 'Monserrate')),
('Visita a la iglesia de Monserrate',     (SELECT id FROM places WHERE name = 'Monserrate')),
('Fotografia panoramica de la ciudad',    (SELECT id FROM places WHERE name = 'Monserrate')),
('Recorrido historico por la plaza',      (SELECT id FROM places WHERE name = 'Plaza de Bolivar')),
('Visita al Capitolio Nacional',          (SELECT id FROM places WHERE name = 'Plaza de Bolivar')),
('Tour por colecciones precolombinas',    (SELECT id FROM places WHERE name = 'Museo del Oro')),
('Taller de orfebres prehispanicos',      (SELECT id FROM places WHERE name = 'Museo del Oro')),
('Recorrido por las murallas',            (SELECT id FROM places WHERE name = 'Castillo de San Felipe')),
('Tour en buggy por la ciudad',           (SELECT id FROM places WHERE name = 'Ciudad Amurallada')),
('Recorrido a pie por calles coloniales', (SELECT id FROM places WHERE name = 'Ciudad Amurallada')),
('Recorrido subterraneo por la mina',     (SELECT id FROM places WHERE name = 'Catedral de Sal de Zipaquira')),
('Fotografia de esculturas de sal',       (SELECT id FROM places WHERE name = 'Catedral de Sal de Zipaquira')),
('Caminata ecologica', (SELECT id FROM places WHERE name = 'Parque Tayrona')),
('Visita a playas naturales', (SELECT id FROM places WHERE name = 'Parque Tayrona')),
('Recorrido historico por el pueblo', (SELECT id FROM places WHERE name = 'Villa de Leyva')),
('Visita a museos coloniales', (SELECT id FROM places WHERE name = 'Villa de Leyva')),
('Recorrido por calles empedradas', (SELECT id FROM places WHERE name = 'Villa de Leyva')),
('Ascenso a la Piedra del Peñol', (SELECT id FROM places WHERE name = 'Guatape')),
('Paseo en lancha por la represa', (SELECT id FROM places WHERE name = 'Guatape'));

-- ── TOURS ────────────────────────────────────────────────────
INSERT INTO tours (name, description, recommendations, price, environment,
    location_city, location_department, location_country, location_latitude, location_longitude,
    meeting_point_city, meeting_point_department, meeting_point_country, meeting_point_latitude, meeting_point_longitude) VALUES
('Tour Bogotá Historica',
    'Recorrido completo por los sitios mas emblematicos del centro historico de Bogotá',
    'Llevar ropa abrigada, calzado comodo e hidratacion',
    150000, 'EXTERIOR',
    'Bogotá','Cundinamarca','Colombia', 4.5981, -74.0759,
    'Bogotá','Cundinamarca','Colombia', 4.5981, -74.0759),
('Bogotá Cultural',
    'Experiencia cultural que combina museos y espacios artisticos de la capital',
    'Se recomienda reservar con anticipacion los museos',
    90000, 'INTERIOR',
    'Bogotá','Cundinamarca','Colombia', 4.6017, -74.0721,
    'Bogotá','Cundinamarca','Colombia', 4.6017, -74.0721),
('Cartagena Colonial',
    'Descubre la magia de la ciudad amurallada y sus fortalezas coloniales',
    'Llevar protector solar y ropa fresca',
    200000, 'EXTERIOR',
    'Cartagena','Bolivar','Colombia', 10.4227, -75.5497,
    'Cartagena','Bolivar','Colombia', 10.4227, -75.5497),
('Aventura en Tayrona',
 'Explora playas y senderos naturales del Parque Tayrona',
 'Usar ropa fresca, bloqueador solar y llevar hidratacion',
 250000, 'EXTERIOR',
 'Santa Marta','Magdalena','Colombia', 11.3000, -73.9500,
 'Santa Marta','Magdalena','Colombia', 11.2408, -74.1990),

('Boyacá Colonial',
 'Recorrido cultural por Villa de Leyva y sus alrededores historicos',
 'Llevar calzado comodo y ropa para clima frio',
 180000, 'EXTERIOR',
 'Villa de Leyva','Boyacá','Colombia', 5.6349, -73.5248,
 'Villa de Leyva','Boyacá','Colombia', 5.6349, -73.5248),

('Guatape Extremo',
 'Tour de aventura y naturaleza en Guatape y la Piedra del Peñol',
 'Se recomienda llevar ropa deportiva',
 220000, 'EXTERIOR',
 'Guatape','Antioquia','Colombia', 6.2342, -75.1635,
 'Guatape','Antioquia','Colombia', 6.2342, -75.1635);

-- ── MEDIA (ALBUM + FOTOS) PARA TOURS ─────────────────────────
INSERT INTO albums (name, current_index) VALUES
('Tour Bogotá Historica', 0),
('Tour Bogotá Cultural', 0),
('Tour Cartagena Colonial', 0),
('Aventura en Tayrona', 0),
('Boyacá Colonial', 0),
('Guatape Extremo', 0);


UPDATE tours SET album_id = (SELECT id FROM albums WHERE name = 'Tour Bogotá Historica') WHERE name = 'Tour Bogotá Historica';
UPDATE tours SET album_id = (SELECT id FROM albums WHERE name = 'Tour Bogotá Cultural')        WHERE name = 'Bogotá Cultural';
UPDATE tours SET album_id = (SELECT id FROM albums WHERE name = 'Tour Cartagena Colonial')    WHERE name = 'Cartagena Colonial';
UPDATE tours SET album_id = (SELECT id FROM albums WHERE name = 'Aventura en Tayrona') WHERE name = 'Aventura en Tayrona';
UPDATE tours SET album_id = (SELECT id FROM albums WHERE name = 'Boyacá Colonial') WHERE name = 'Boyacá Colonial';
UPDATE tours SET album_id = (SELECT id FROM albums WHERE name = 'Guatape Extremo') WHERE name = 'Guatape Extremo';

INSERT INTO photos (file_path, photo_description, album_id) VALUES
('https://radionacional-v3.s3.amazonaws.com/s3fs-public/node/article/field_image/MONSERRATE.jpg',
 'Vista panoramica desde Monserrate',
 (SELECT id FROM albums WHERE name = 'Tour Bogotá Historica')),
('https://tse1.mm.bing.net/th/id/OIP.eQHklS0cup4D_9xpmTP_pgHaLH?pid=ImgDet&w=474&h=711&rs=1&o=7&rm=3',
 'Teleférico de Monserrate',
 (SELECT id FROM albums WHERE name = 'Tour Bogotá Historica')),
('https://tse2.mm.bing.net/th/id/OIP.LmSJFHLfNu3dUVLb-z5LuwHaFI?rs=1&pid=ImgDetMain&o=7&rm=3',
 'Coleccion de oro en el Museo del Oro',
 (SELECT id FROM albums WHERE name = 'Tour Bogotá Cultural')),
('https://cartagenaplay.com/wp-content/uploads/9008013895_5a53127df8_o-scaled.jpg',
 'Ciudad amurallada de Cartagena',
 (SELECT id FROM albums WHERE name = 'Tour Cartagena Colonial')),
('https://www.beautifulworld.com/wp-content/uploads/2017/03/parque_tayrona_jungle.jpg',
 'Paisajes del Tayrona',
 (SELECT id FROM albums WHERE name = 'Aventura en Tayrona')),

('https://upload.wikimedia.org/wikipedia/commons/5/5c/Villa_de_Leyva.jpg',
 'Calles coloniales de Boyacá',
 (SELECT id FROM albums WHERE name = 'Boyacá Colonial')),

('https://upload.wikimedia.org/wikipedia/commons/0/0d/Guatape-Antioquia.jpg',
 'Vista desde la Piedra del Peñol',
 (SELECT id FROM albums WHERE name = 'Guatape Extremo'));

-- ── TOUR OFFERS ──────────────────────────────────────────────
INSERT INTO tour_offers (tour_id, base_price) VALUES
((SELECT id FROM tours WHERE name = 'Tour Bogotá Historica'), 120000),
((SELECT id FROM tours WHERE name = 'Bogotá Cultural'),        70000),
((SELECT id FROM tours WHERE name = 'Cartagena Colonial'),    160000),
((SELECT id FROM tours WHERE name = 'Aventura en Tayrona'), 200000),
((SELECT id FROM tours WHERE name = 'Boyacá Colonial'), 150000),
((SELECT id FROM tours WHERE name = 'Guatape Extremo'), 220000);

-- ── ITINERARIOS ──────────────────────────────────────────────
INSERT INTO itinerary (tour_id, tourist_place_id, position) VALUES
((SELECT id FROM tours WHERE name = 'Tour Bogotá Historica'), (SELECT id FROM places WHERE name = 'Plaza de Bolivar'), 1),
((SELECT id FROM tours WHERE name = 'Tour Bogotá Historica'), (SELECT id FROM places WHERE name = 'Monserrate'),       2),
((SELECT id FROM tours WHERE name = 'Bogotá Cultural'),       (SELECT id FROM places WHERE name = 'Museo del Oro'),    1),
((SELECT id FROM tours WHERE name = 'Cartagena Colonial'),    (SELECT id FROM places WHERE name = 'Ciudad Amurallada'),1),
((SELECT id FROM tours WHERE name = 'Cartagena Colonial'),    (SELECT id FROM places WHERE name = 'Castillo de San Felipe'), 2),
((SELECT id FROM tours WHERE name = 'Aventura en Tayrona'),
 (SELECT id FROM places WHERE name = 'Parque Tayrona'), 1),
((SELECT id FROM tours WHERE name = 'Boyacá Colonial'),
 (SELECT id FROM places WHERE name = 'Villa de Leyva'), 1),
((SELECT id FROM tours WHERE name = 'Guatape Extremo'),
 (SELECT id FROM places WHERE name = 'Guatape'), 1);

-- ── TOUR ↔ CATEGORY ───────────────────────────────────────────
INSERT INTO tour_categories (tour_id, category_id) VALUES
((SELECT id FROM tours WHERE name = 'Tour Bogotá Historica'),
 (SELECT id FROM categories WHERE name = 'Historia')),

((SELECT id FROM tours WHERE name = 'Tour Bogotá Historica'),
 (SELECT id FROM categories WHERE name = 'Cultura')),

((SELECT id FROM tours WHERE name = 'Tour Bogotá Historica'),
 (SELECT id FROM categories WHERE name = 'Religión')),

((SELECT id FROM tours WHERE name = 'Bogotá Cultural'),
 (SELECT id FROM categories WHERE name = 'Cultura')),

((SELECT id FROM tours WHERE name = 'Bogotá Cultural'),
 (SELECT id FROM categories WHERE name = 'Arte')),

((SELECT id FROM tours WHERE name = 'Bogotá Cultural'),
 (SELECT id FROM categories WHERE name = 'Historia')),

((SELECT id FROM tours WHERE name = 'Cartagena Colonial'),
 (SELECT id FROM categories WHERE name = 'Historia')),

((SELECT id FROM tours WHERE name = 'Cartagena Colonial'),
 (SELECT id FROM categories WHERE name = 'Cultura')),

((SELECT id FROM tours WHERE name = 'Cartagena Colonial'),
 (SELECT id FROM categories WHERE name = 'Aventura')),

((SELECT id FROM tours WHERE name = 'Aventura en Tayrona'),
 (SELECT id FROM categories WHERE name = 'Naturaleza')),

((SELECT id FROM tours WHERE name = 'Aventura en Tayrona'),
 (SELECT id FROM categories WHERE name = 'Aventura')),

((SELECT id FROM tours WHERE name = 'Boyacá Colonial'),
 (SELECT id FROM categories WHERE name = 'Historia')),

((SELECT id FROM tours WHERE name = 'Boyacá Colonial'),
 (SELECT id FROM categories WHERE name = 'Cultura')),

((SELECT id FROM tours WHERE name = 'Guatape Extremo'),
 (SELECT id FROM categories WHERE name = 'Aventura')),

((SELECT id FROM tours WHERE name = 'Guatape Extremo'),
 (SELECT id FROM categories WHERE name = 'Naturaleza'));

-- ── PLACE ↔ CATEGORY ───────────────────────────────
INSERT INTO places_categories (place_id, category_id) VALUES

((SELECT id FROM places WHERE name = 'Plaza de Bolivar'),
 (SELECT id FROM categories WHERE name = 'Historia')),

((SELECT id FROM places WHERE name = 'Plaza de Bolivar'),
 (SELECT id FROM categories WHERE name = 'Cultura')),

((SELECT id FROM places WHERE name = 'Museo del Oro'),
 (SELECT id FROM categories WHERE name = 'Historia')),

((SELECT id FROM places WHERE name = 'Museo del Oro'),
 (SELECT id FROM categories WHERE name = 'Arte')),

((SELECT id FROM places WHERE name = 'Museo del Oro'),
 (SELECT id FROM categories WHERE name = 'Cultura')),

((SELECT id FROM places WHERE name = 'Ciudad Amurallada'),
 (SELECT id FROM categories WHERE name = 'Historia')),

((SELECT id FROM places WHERE name = 'Ciudad Amurallada'),
 (SELECT id FROM categories WHERE name = 'Cultura')),

((SELECT id FROM places WHERE name = 'Ciudad Amurallada'),
 (SELECT id FROM categories WHERE name = 'Gastronomía'));

-- ── TOURS GUARDADOS ──────────────────────────────────────────
INSERT INTO saved_tours (user_id, tour_id) VALUES
((SELECT u.id FROM users u JOIN user_profiles p ON u.user_profile_id = p.id WHERE p.document = '1234567890'),
 (SELECT id FROM tours WHERE name = 'Tour Bogotá Historica')),
((SELECT u.id FROM users u JOIN user_profiles p ON u.user_profile_id = p.id WHERE p.document = '1234567890'),
 (SELECT id FROM tours WHERE name = 'Bogotá Cultural')),
((SELECT u.id FROM users u JOIN user_profiles p ON u.user_profile_id = p.id WHERE p.document = '9876543210'),
 (SELECT id FROM tours WHERE name = 'Cartagena Colonial'));

-- ── RESEÑAS ──────────────────────────────────────────────────
INSERT INTO reviews (author_id, tour_id, rating, comment, publication_date) VALUES
((SELECT u.id FROM users u JOIN user_profiles p ON u.user_profile_id = p.id WHERE p.document = '1234567890'),
 (SELECT id FROM tours WHERE name = 'Tour Bogotá Historica'), 5,
 'Increible experiencia, lo recomiendo totalmente. Las vistas desde Monserrate son espectaculares.', '2026-04-10'),
((SELECT u.id FROM users u JOIN user_profiles p ON u.user_profile_id = p.id WHERE p.document = '1234567890'),
 (SELECT id FROM tours WHERE name = 'Bogotá Cultural'), 4,
 'Muy buen recorrido cultural. El Museo del Oro supero mis expectativas.', '2026-04-15'),
((SELECT u.id FROM users u JOIN user_profiles p ON u.user_profile_id = p.id WHERE p.document = '9876543210'),
 (SELECT id FROM tours WHERE name = 'Cartagena Colonial'), 5,
 'Cartagena es magica. El guia fue excelente y la organizacion perfecta.', '2026-05-01');

-- ── PLACE ↔ CATEGORY ───────────────────────────────
INSERT INTO places_categories (place_id, category_id) VALUES

-- Monserrate
((SELECT id FROM places WHERE name = 'Monserrate'),
 (SELECT id FROM categories WHERE name = 'Naturaleza')),

((SELECT id FROM places WHERE name = 'Monserrate'),
 (SELECT id FROM categories WHERE name = 'Religión')),

((SELECT id FROM places WHERE name = 'Monserrate'),
 (SELECT id FROM categories WHERE name = 'Aventura')),

-- Plaza de Bolívar
((SELECT id FROM places WHERE name = 'Plaza de Bolivar'),
 (SELECT id FROM categories WHERE name = 'Historia')),

((SELECT id FROM places WHERE name = 'Plaza de Bolivar'),
 (SELECT id FROM categories WHERE name = 'Cultura')),

-- Museo del Oro
((SELECT id FROM places WHERE name = 'Museo del Oro'),
 (SELECT id FROM categories WHERE name = 'Historia')),

((SELECT id FROM places WHERE name = 'Museo del Oro'),
 (SELECT id FROM categories WHERE name = 'Arte')),

((SELECT id FROM places WHERE name = 'Museo del Oro'),
 (SELECT id FROM categories WHERE name = 'Cultura')),

-- Castillo de San Felipe
((SELECT id FROM places WHERE name = 'Castillo de San Felipe'),
 (SELECT id FROM categories WHERE name = 'Historia')),

((SELECT id FROM places WHERE name = 'Castillo de San Felipe'),
 (SELECT id FROM categories WHERE name = 'Aventura')),

-- Ciudad Amurallada
((SELECT id FROM places WHERE name = 'Ciudad Amurallada'),
 (SELECT id FROM categories WHERE name = 'Historia')),

((SELECT id FROM places WHERE name = 'Ciudad Amurallada'),
 (SELECT id FROM categories WHERE name = 'Cultura')),

((SELECT id FROM places WHERE name = 'Ciudad Amurallada'),
 (SELECT id FROM categories WHERE name = 'Gastronomía')),

((SELECT id FROM places WHERE name = 'Catedral de Sal de Zipaquira'),
 (SELECT id FROM categories WHERE name = 'Historia')),

((SELECT id FROM places WHERE name = 'Catedral de Sal de Zipaquira'),
 (SELECT id FROM categories WHERE name = 'Religión')),

((SELECT id FROM places WHERE name = 'Parque Tayrona'),
 (SELECT id FROM categories WHERE name = 'Naturaleza')),

((SELECT id FROM places WHERE name = 'Parque Tayrona'),
 (SELECT id FROM categories WHERE name = 'Aventura')),

((SELECT id FROM places WHERE name = 'Villa de Leyva'),
 (SELECT id FROM categories WHERE name = 'Historia')),

((SELECT id FROM places WHERE name = 'Villa de Leyva'),
 (SELECT id FROM categories WHERE name = 'Cultura')),

((SELECT id FROM places WHERE name = 'Guatape'),
 (SELECT id FROM categories WHERE name = 'Aventura')),

((SELECT id FROM places WHERE name = 'Guatape'),
 (SELECT id FROM categories WHERE name = 'Naturaleza'));
