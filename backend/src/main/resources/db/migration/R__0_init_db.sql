CREATE TABLE IF NOT EXISTS `train` (
  `id` serial primary key,
   `name` varchar(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS `ticket` (
  `id` serial primary key,
   `train_id` int NOT NULL
);