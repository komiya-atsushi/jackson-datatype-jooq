CREATE TABLE planet (
  planet_id INTEGER PRIMARY KEY,
  name VARCHAR (32) NOT NULL,
  mean_radius_km DOUBLE NOT NULL,
  is_inner_planet BOOLEAN NOT NULL,
  discovery_date DATE
);

INSERT INTO planet (planet_id, name, mean_radius_km, is_inner_planet, discovery_date) VALUES
  (1, 'Mercury', 2439.7, true, null),
  (2, 'Venus', 6051.8, true, null),
  (3, 'Earth', 6371.0, true, null),
  (4, 'Mars', 3389.5, true, null),
  (5, 'Jupiter', 69911, false, null),
  (6, 'Saturn', 58232, false, null),
  (7, 'Uranus', 25362, false, '1781-03-13'),
  (8, 'Neptune', 24622, false, '1846-09-23')
;