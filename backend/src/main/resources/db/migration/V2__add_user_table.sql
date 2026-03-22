CREATE TABLE IF NOT EXISTS `user` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY,
    `username` VARCHAR(50) NOT NULL UNIQUE,
    `password` VARCHAR(255) NOT NULL,
    `full_name` VARCHAR(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 插入测试用户: alice/password123, bob/password123
-- 密码使用 BCrypt 加密
INSERT INTO `user` (username, password, full_name) VALUES
    ('alice', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36ZfPIlwiNJDt0e2p6qGmQe', 'Alice Wang'),
    ('bob', '$2a$10$EixZaYVK1fsbw1ZfbX3OXePaWxn96p36ZfPIlwiNJDt0e2p6qGmQe', 'Bob Chen');
