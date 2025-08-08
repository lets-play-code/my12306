-- Reset and seed demo data for manual verification on every startup (dev)

-- Optional: disable FK checks during truncation (safe even if no FKs defined)
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;

TRUNCATE TABLE ticket;
TRUNCATE TABLE seat;
TRUNCATE TABLE stop;
TRUNCATE TABLE train;

SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;

-- Deterministic inserts so IDs start from 1 in a clean schema
INSERT INTO train (name) VALUES ('G102');
INSERT INTO train (name) VALUES ('G103');

-- Stops for G102: 北京南 -> 南京南 -> 镇江 -> 上海虹桥
INSERT INTO stop (name, `order`, train_id)
SELECT '北京南', 1, t.id FROM train t WHERE t.name = 'G102';
INSERT INTO stop (name, `order`, train_id)
SELECT '南京南', 2, t.id FROM train t WHERE t.name = 'G102';
INSERT INTO stop (name, `order`, train_id)
SELECT '镇江', 3, t.id FROM train t WHERE t.name = 'G102';
INSERT INTO stop (name, `order`, train_id)
SELECT '上海虹桥', 4, t.id FROM train t WHERE t.name = 'G102';

-- Stops for G103: 上海虹桥 -> 北京南
INSERT INTO stop (name, `order`, train_id)
SELECT '上海虹桥', 1, t.id FROM train t WHERE t.name = 'G103';
INSERT INTO stop (name, `order`, train_id)
SELECT '北京南', 2, t.id FROM train t WHERE t.name = 'G103';

-- Seats for G102
INSERT INTO seat (name, train_id)
SELECT '2D4', t.id FROM train t WHERE t.name = 'G102';
INSERT INTO seat (name, train_id)
SELECT '2D5', t.id FROM train t WHERE t.name = 'G102';

-- Seats for G103
INSERT INTO seat (name, train_id)
SELECT '2D4', t.id FROM train t WHERE t.name = 'G103';

-- No tickets seeded by default so that all seats are initially available for manual testing.
