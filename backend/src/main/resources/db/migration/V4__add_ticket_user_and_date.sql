ALTER TABLE `ticket` ADD COLUMN `user_id` BIGINT NOT NULL;
ALTER TABLE `ticket` ADD COLUMN `travel_date` DATE NOT NULL;

-- 添加外键约束
ALTER TABLE `ticket` ADD CONSTRAINT `fk_ticket_user` FOREIGN KEY (`user_id`) REFERENCES `user`(`id`);
