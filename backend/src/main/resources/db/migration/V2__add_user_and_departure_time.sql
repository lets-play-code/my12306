ALTER TABLE stop ADD COLUMN departure_time TIME NULL;

CREATE TABLE `user` (
  `id` serial primary key,
  `username` varchar(255) NOT NULL UNIQUE,
  `password` varchar(255) NOT NULL,
  `full_name` varchar(255) NOT NULL
);

ALTER TABLE ticket ADD COLUMN user_id int NULL REFERENCES `user`(id);

-- Seed test user
INSERT INTO `user` (username, password, full_name) VALUES ('testuser', 'password123', 'Test User');
