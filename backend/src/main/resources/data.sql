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
-- admin123 / user123
INSERT INTO credentials (email, password, state, role, user_id) VALUES
('admin@gmail.com', '$2a$10$49zGGo/7F3ItCvTAY22/veJCDQkncwZHDB3YqW9hvAFNFcNrTYJCC', 'ACTIVE', 'ADMINISTRATOR',
    (SELECT u.id FROM users u JOIN user_profiles p ON u.user_profile_id = p.id WHERE p.document = '1053489671')),
('user@gmail.com',  '$2a$10$lTux.l0yHzDZSlGzwRiUJedWM9u.HP1IlbWC287yLzcINWjnFDb0i', 'ACTIVE', 'USER',
    (SELECT u.id FROM users u JOIN user_profiles p ON u.user_profile_id = p.id WHERE p.document = '1234567890')),
('maria@gmail.com', '$2a$10$lTux.l0yHzDZSlGzwRiUJedWM9u.HP1IlbWC287yLzcINWjnFDb0i', 'ACTIVE', 'USER',
    (SELECT u.id FROM users u JOIN user_profiles p ON u.user_profile_id = p.id WHERE p.document = '9876543210'));

-- ── LUGARES TURÍSTICOS ───────────────────────────────────────
INSERT INTO places (name, description, duration, environment, city, department, country, latitude, longitude) VALUES
('Monserrate',             'Monserrate es uno de los sitios turisticos y religiosos mas representativos de Bogota. Ubicado a mas de 3.100 metros sobre el nivel del mar',             '3 horas', 'EXTERIOR', 'Bogota',    'Cundinamarca', 'Colombia',   4.6072539, -74.0543090),
('Plaza de Bolivar',       'La Plaza de Bolivar es el corazon historico y politico de Colombia. Rodeada por edificios emblematicos como el Capitolio Nacional',        '1 hora',  'EXTERIOR', 'Bogota',    'Cundinamarca', 'Colombia',  4.5981, -74.0759),
('Museo del Oro',          'Museo con la mayor coleccion de piezas precolombinas en oro',  '2 horas', 'INTERIOR', 'Bogota',    'Cundinamarca', 'Colombia',  4.6017, -74.0721),
('Castillo de San Felipe', 'Fortaleza colonial del siglo XVII, Patrimonio de la Humanidad','2 horas', 'MIXED', 'Cartagena', 'Bolivar',      'Colombia', 10.4236, -75.5380),
('Ciudad Amurallada',      'Centro historico de Cartagena rodeado de murallas coloniales', '3 horas', 'EXTERIOR', 'Cartagena', 'Bolivar',      'Colombia', 10.4227, -75.5497);

-- ── MEDIA (ALBUM + FOTOS) PARA LUGARES ───────────────────────
INSERT INTO albums (name, current_index) VALUES
('Monserrate', 0),
('Plaza de Bolivar', 0),
('Museo del Oro', 0),
('Castillo de San Felipe', 0),
('Ciudad Amurallada', 0);

UPDATE places SET album_id = (SELECT id FROM albums WHERE name = 'Monserrate')             WHERE name = 'Monserrate';
UPDATE places SET album_id = (SELECT id FROM albums WHERE name = 'Plaza de Bolivar')       WHERE name = 'Plaza de Bolivar';
UPDATE places SET album_id = (SELECT id FROM albums WHERE name = 'Museo del Oro')          WHERE name = 'Museo del Oro';
UPDATE places SET album_id = (SELECT id FROM albums WHERE name = 'Castillo de San Felipe') WHERE name = 'Castillo de San Felipe';
UPDATE places SET album_id = (SELECT id FROM albums WHERE name = 'Ciudad Amurallada')      WHERE name = 'Ciudad Amurallada';

INSERT INTO photos (file_path, photo_description, album_id) VALUES
('https://radionacional-v3.s3.amazonaws.com/s3fs-public/node/article/field_image/MONSERRATE.jpg',
 'Vista panoramica desde Monserrate',
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
 (SELECT id FROM albums WHERE name = 'Castillo de San Felipe'));

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
('Recorrido a pie por calles coloniales', (SELECT id FROM places WHERE name = 'Ciudad Amurallada'));

-- ── TOURS ────────────────────────────────────────────────────
INSERT INTO tours (name, description, recommendations, price, environment,
    location_city, location_department, location_country, location_latitude, location_longitude,
    meeting_point_city, meeting_point_department, meeting_point_country, meeting_point_latitude, meeting_point_longitude) VALUES
('Tour Bogota Historica',
    'Recorrido completo por los sitios mas emblematicos del centro historico de Bogota',
    'Llevar ropa abrigada, calzado comodo e hidratacion',
    150000, 'EXTERIOR',
    'Bogota','Cundinamarca','Colombia', 4.5981, -74.0759,
    'Bogota','Cundinamarca','Colombia', 4.5981, -74.0759),
('Bogota Cultural',
    'Experiencia cultural que combina museos y espacios artisticos de la capital',
    'Se recomienda reservar con anticipacion los museos',
    90000, 'INTERIOR',
    'Bogota','Cundinamarca','Colombia', 4.6017, -74.0721,
    'Bogota','Cundinamarca','Colombia', 4.6017, -74.0721),
('Cartagena Colonial',
    'Descubre la magia de la ciudad amurallada y sus fortalezas coloniales',
    'Llevar protector solar y ropa fresca',
    200000, 'EXTERIOR',
    'Cartagena','Bolivar','Colombia', 10.4227, -75.5497,
    'Cartagena','Bolivar','Colombia', 10.4227, -75.5497);

-- ── MEDIA (ALBUM + FOTOS) PARA TOURS ─────────────────────────
INSERT INTO albums (name, current_index) VALUES
('Tour Bogota Historica', 0),
('Tour Bogota Cultural', 0),
('Tour Cartagena Colonial', 0);

UPDATE tours SET album_id = (SELECT id FROM albums WHERE name = 'Tour Bogota Historica') WHERE name = 'Tour Bogota Historica';
UPDATE tours SET album_id = (SELECT id FROM albums WHERE name = 'Tour Bogota Cultural')        WHERE name = 'Bogota Cultural';
UPDATE tours SET album_id = (SELECT id FROM albums WHERE name = 'Tour Cartagena Colonial')    WHERE name = 'Cartagena Colonial';

INSERT INTO photos (file_path, photo_description, album_id) VALUES
('https://radionacional-v3.s3.amazonaws.com/s3fs-public/node/article/field_image/MONSERRATE.jpg',
 'Vista panoramica desde Monserrate',
 (SELECT id FROM albums WHERE name = 'Tour Bogota Historica')),
('https://tse2.mm.bing.net/th/id/OIP.LmSJFHLfNu3dUVLb-z5LuwHaFI?rs=1&pid=ImgDetMain&o=7&rm=3',
 'Coleccion de oro en el Museo del Oro',
 (SELECT id FROM albums WHERE name = 'Tour Bogota Cultural')),
('https://cartagenaplay.com/wp-content/uploads/9008013895_5a53127df8_o-scaled.jpg',
 'Ciudad amurallada de Cartagena',
 (SELECT id FROM albums WHERE name = 'Tour Cartagena Colonial'));


-- ── TOUR OFFERS ──────────────────────────────────────────────
INSERT INTO tour_offers (tour_id, base_price) VALUES
((SELECT id FROM tours WHERE name = 'Tour Bogota Historica'), 120000),
((SELECT id FROM tours WHERE name = 'Bogota Cultural'),        70000),
((SELECT id FROM tours WHERE name = 'Cartagena Colonial'),    160000);

-- ── ITINERARIOS ──────────────────────────────────────────────
INSERT INTO itinerary (tour_id, tourist_place_id, position) VALUES
((SELECT id FROM tours WHERE name = 'Tour Bogota Historica'), (SELECT id FROM places WHERE name = 'Plaza de Bolivar'), 1),
((SELECT id FROM tours WHERE name = 'Tour Bogota Historica'), (SELECT id FROM places WHERE name = 'Monserrate'),       2),
((SELECT id FROM tours WHERE name = 'Bogota Cultural'),       (SELECT id FROM places WHERE name = 'Museo del Oro'),    1),
((SELECT id FROM tours WHERE name = 'Cartagena Colonial'),    (SELECT id FROM places WHERE name = 'Ciudad Amurallada'),1),
((SELECT id FROM tours WHERE name = 'Cartagena Colonial'),    (SELECT id FROM places WHERE name = 'Castillo de San Felipe'), 2);

-- ── TOURS GUARDADOS ──────────────────────────────────────────
INSERT INTO saved_tours (user_id, tour_id) VALUES
((SELECT u.id FROM users u JOIN user_profiles p ON u.user_profile_id = p.id WHERE p.document = '1234567890'),
 (SELECT id FROM tours WHERE name = 'Tour Bogota Historica')),
((SELECT u.id FROM users u JOIN user_profiles p ON u.user_profile_id = p.id WHERE p.document = '1234567890'),
 (SELECT id FROM tours WHERE name = 'Bogota Cultural')),
((SELECT u.id FROM users u JOIN user_profiles p ON u.user_profile_id = p.id WHERE p.document = '9876543210'),
 (SELECT id FROM tours WHERE name = 'Cartagena Colonial'));

-- ── RESEÑAS ──────────────────────────────────────────────────
INSERT INTO reviews (author_id, tour_id, rating, comment, publication_date) VALUES
((SELECT u.id FROM users u JOIN user_profiles p ON u.user_profile_id = p.id WHERE p.document = '1234567890'),
 (SELECT id FROM tours WHERE name = 'Tour Bogota Historica'), 5,
 'Increible experiencia, lo recomiendo totalmente. Las vistas desde Monserrate son espectaculares.', '2026-04-10'),
((SELECT u.id FROM users u JOIN user_profiles p ON u.user_profile_id = p.id WHERE p.document = '1234567890'),
 (SELECT id FROM tours WHERE name = 'Bogota Cultural'), 4,
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
 (SELECT id FROM categories WHERE name = 'Gastronomía'));