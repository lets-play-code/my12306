-- Create test database and grant privileges
-- This script runs automatically when MySQL container starts

CREATE DATABASE IF NOT EXISTS db_test;
GRANT ALL PRIVILEGES ON db_test.* TO 'admin'@'%';
FLUSH PRIVILEGES;
