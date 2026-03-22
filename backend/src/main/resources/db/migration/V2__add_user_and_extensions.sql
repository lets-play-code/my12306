-- V1: Add user table and new columns to existing tables

-- Create user table
CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(50) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL,
    `full_name` VARCHAR(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- Add columns to train table
ALTER TABLE `train` ADD COLUMN `departure_time` TIME NOT NULL DEFAULT '08:00:00';
UPDATE `train` SET `departure_time` = '08:00:00' WHERE `name` = 'G102';
UPDATE `train` SET `departure_time` = '14:30:00' WHERE `name` = 'G103';

-- Add columns to ticket table
ALTER TABLE `ticket` ADD COLUMN `user_id` BIGINT NOT NULL;
ALTER TABLE `ticket` ADD COLUMN `travel_date` DATE NOT NULL;

-- Add foreign key
ALTER TABLE `ticket` ADD CONSTRAINT `fk_ticket_user` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`);

-- Seed test users
DELETE FROM `user` WHERE username IN ('alice', 'bob');
INSERT INTO `user` (username, password, full_name) VALUES
    ('alice', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36ZfPIlwiNJDt0e2p6qGmQe', 'Alice Wang'),
    ('bob', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36ZfPIlwiNJDt0e2p6qGmQe', 'Bob Chen');
