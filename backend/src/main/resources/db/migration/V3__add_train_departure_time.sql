ALTER TABLE `train` ADD COLUMN `departure_time` TIME NOT NULL DEFAULT '08:00:00';

-- 更新现有火车的发车时间
UPDATE `train` SET `departure_time` = '08:00:00' WHERE `name` = 'G102';
UPDATE `train` SET `departure_time` = '14:30:00' WHERE `name` = 'G103';
