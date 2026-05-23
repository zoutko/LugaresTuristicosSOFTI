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
('Monserrate',             'Cerro emblematico de Bogota con vista panoramica',             '3 horas', 'EXTERIOR', 'Bogota',    'Cundinamarca', 'Colombia',  4.7110, -74.0560),
('Plaza de Bolivar',       'Plaza principal de Bogota, corazon historico del pais',        '1 hora',  'EXTERIOR', 'Bogota',    'Cundinamarca', 'Colombia',  4.5981, -74.0759),
('Museo del Oro',          'Museo con la mayor coleccion de piezas precolombinas en oro',  '2 horas', 'INTERIOR', 'Bogota',    'Cundinamarca', 'Colombia',  4.6017, -74.0721),
('Castillo de San Felipe', 'Fortaleza colonial del siglo XVII, Patrimonio de la Humanidad','2 horas', 'EXTERIOR', 'Cartagena', 'Bolivar',      'Colombia', 10.4236, -75.5380),
('Ciudad Amurallada',      'Centro historico de Cartagena rodeado de murallas coloniales', '3 horas', 'EXTERIOR', 'Cartagena', 'Bolivar',      'Colombia', 10.4227, -75.5497);

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

 -- ── USUARIOS DE PRUEBA JMETER (contraseña: test1234) ─────────
INSERT INTO user_profiles (name, document, role_name) VALUES
('JMeter User 01', '2000000001', 'USER'),
('JMeter User 02', '2000000002', 'USER'),
('JMeter User 03', '2000000003', 'USER'),
('JMeter User 04', '2000000004', 'USER'),
('JMeter User 05', '2000000005', 'USER'),
('JMeter User 06', '2000000006', 'USER'),
('JMeter User 07', '2000000007', 'USER'),
('JMeter User 08', '2000000008', 'USER'),
('JMeter User 09', '2000000009', 'USER'),
('JMeter User 10', '2000000010', 'USER');

INSERT INTO contacts (phone_number, user_profile_id) VALUES
('3100000001', (SELECT id FROM user_profiles WHERE document = '2000000001')),
('3100000002', (SELECT id FROM user_profiles WHERE document = '2000000002')),
('3100000003', (SELECT id FROM user_profiles WHERE document = '2000000003')),
('3100000004', (SELECT id FROM user_profiles WHERE document = '2000000004')),
('3100000005', (SELECT id FROM user_profiles WHERE document = '2000000005')),
('3100000006', (SELECT id FROM user_profiles WHERE document = '2000000006')),
('3100000007', (SELECT id FROM user_profiles WHERE document = '2000000007')),
('3100000008', (SELECT id FROM user_profiles WHERE document = '2000000008')),
('3100000009', (SELECT id FROM user_profiles WHERE document = '2000000009')),
('3100000010', (SELECT id FROM user_profiles WHERE document = '2000000010'));

INSERT INTO users (user_profile_id) VALUES
((SELECT id FROM user_profiles WHERE document = '2000000001')),
((SELECT id FROM user_profiles WHERE document = '2000000002')),
((SELECT id FROM user_profiles WHERE document = '2000000003')),
((SELECT id FROM user_profiles WHERE document = '2000000004')),
((SELECT id FROM user_profiles WHERE document = '2000000005')),
((SELECT id FROM user_profiles WHERE document = '2000000006')),
((SELECT id FROM user_profiles WHERE document = '2000000007')),
((SELECT id FROM user_profiles WHERE document = '2000000008')),
((SELECT id FROM user_profiles WHERE document = '2000000009')),
((SELECT id FROM user_profiles WHERE document = '2000000010'));

INSERT INTO credentials (email, password, state, role, user_id) VALUES
('jmeter01@test.com', '$2a$10$lTux.l0yHzDZSlGzwRiUJedWM9u.HP1IlbWC287yLzcINWjnFDb0i', 'ACTIVE', 'USER', (SELECT u.id FROM users u JOIN user_profiles p ON u.user_profile_id = p.id WHERE p.document = '2000000001')),
('jmeter02@test.com', '$2a$10$lTux.l0yHzDZSlGzwRiUJedWM9u.HP1IlbWC287yLzcINWjnFDb0i', 'ACTIVE', 'USER', (SELECT u.id FROM users u JOIN user_profiles p ON u.user_profile_id = p.id WHERE p.document = '2000000002')),
('jmeter03@test.com', '$2a$10$lTux.l0yHzDZSlGzwRiUJedWM9u.HP1IlbWC287yLzcINWjnFDb0i', 'ACTIVE', 'USER', (SELECT u.id FROM users u JOIN user_profiles p ON u.user_profile_id = p.id WHERE p.document = '2000000003')),
('jmeter04@test.com', '$2a$10$lTux.l0yHzDZSlGzwRiUJedWM9u.HP1IlbWC287yLzcINWjnFDb0i', 'ACTIVE', 'USER', (SELECT u.id FROM users u JOIN user_profiles p ON u.user_profile_id = p.id WHERE p.document = '2000000004')),
('jmeter05@test.com', '$2a$10$lTux.l0yHzDZSlGzwRiUJedWM9u.HP1IlbWC287yLzcINWjnFDb0i', 'ACTIVE', 'USER', (SELECT u.id FROM users u JOIN user_profiles p ON u.user_profile_id = p.id WHERE p.document = '2000000005')),
('jmeter06@test.com', '$2a$10$lTux.l0yHzDZSlGzwRiUJedWM9u.HP1IlbWC287yLzcINWjnFDb0i', 'ACTIVE', 'USER', (SELECT u.id FROM users u JOIN user_profiles p ON u.user_profile_id = p.id WHERE p.document = '2000000006')),
('jmeter07@test.com', '$2a$10$lTux.l0yHzDZSlGzwRiUJedWM9u.HP1IlbWC287yLzcINWjnFDb0i', 'ACTIVE', 'USER', (SELECT u.id FROM users u JOIN user_profiles p ON u.user_profile_id = p.id WHERE p.document = '2000000007')),
('jmeter08@test.com', '$2a$10$lTux.l0yHzDZSlGzwRiUJedWM9u.HP1IlbWC287yLzcINWjnFDb0i', 'ACTIVE', 'USER', (SELECT u.id FROM users u JOIN user_profiles p ON u.user_profile_id = p.id WHERE p.document = '2000000008')),
('jmeter09@test.com', '$2a$10$lTux.l0yHzDZSlGzwRiUJedWM9u.HP1IlbWC287yLzcINWjnFDb0i', 'ACTIVE', 'USER', (SELECT u.id FROM users u JOIN user_profiles p ON u.user_profile_id = p.id WHERE p.document = '2000000009')),
('jmeter10@test.com', '$2a$10$lTux.l0yHzDZSlGzwRiUJedWM9u.HP1IlbWC287yLzcINWjnFDb0i', 'ACTIVE', 'USER', (SELECT u.id FROM users u JOIN user_profiles p ON u.user_profile_id = p.id WHERE p.document = '2000000010'));
SELECT email FROM credentials WHERE email LIKE 'jmeter%';
