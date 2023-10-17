DROP TABLE IF EXISTS `train`;
CREATE TABLE `train` (
  `id` serial primary key,
   `name` varchar(255) NOT NULL
);

DROP TABLE IF EXISTS `seat`;
CREATE TABLE `seat` (
  `id` serial primary key,
   `train_id` int NOT NULL,
   `name` varchar(255) NOT NULL
);

DROP TABLE IF EXISTS `ticket`;
CREATE TABLE `ticket` (
  `id` serial primary key,
   `seat_id` int NOT NULL,
   `from_id` int NOT NULL,
   `to_id` int NOT NULL
);

DROP TABLE IF EXISTS `stop`;
CREATE TABLE `stop` (
  `id` serial primary key,
   `name` varchar(255) NOT NULL,
   `order` int NOT NULL,
   `train_id` int NOT NULL
);
