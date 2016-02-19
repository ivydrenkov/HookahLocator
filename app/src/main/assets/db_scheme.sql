
CREATE TABLE IF NOT EXISTS `cities` (
	`id` INTEGER UNIQUE,
	`name` TEXT,
	`abbr` TEXT,
	`hookahsCount` INTEGER,
	`latitude` DOUBLE,
	`longitude` DOUBLE
);


CREATE TABLE IF NOT EXISTS `places` (
	`id` INTEGER UNIQUE,
	`name` TEXT,
	`rate` TEXT,
	`currency` TEXT,
	`country` TEXT,
	`address` TEXT,
	`metro` TEXT,
	`logo` TEXT,
	`main_image` TEXT,
	`latitude` DOUBLE,
	`longitude` DOUBLE
);

CREATE TABLE IF NOT EXISTS `places_data` (
	`id` INTEGER UNIQUE,
	`name` TEXT,
	`rate_all` TEXT,
	`rate_atmosphere` TEXT,
	`rate_calian` TEXT,
	`rate_service` TEXT,
	`rates` INTEGER,
	`currency` TEXT,
	`cost_calian` TEXT,
	`cost_tea` TEXT,
	`food` TEXT,
	`drinks` TEXT,
	`address` TEXT,
	`metro` TEXT,
    `phone` TEXT,
    `is_alltime` TEXT,
    `open` TEXT,
    `close` TEXT,
    `weekend_open` TEXT,
    `weekend_close` TEXT,
    `working` TEXT,
    `w_working` TEXT,
	`logo` TEXT,
	`main_image` TEXT,
	`photos_json_array` TEXT,
	`tags_json_array` TEXT,
	`comments_json_array` TEXT,
	`latitude` DOUBLE,
	`longitude` DOUBLE
);

CREATE TABLE IF NOT EXISTS `places_additional` (
	`id` INTEGER UNIQUE,
	`costHookah` TEXT,
	`costTea` TEXT,
	`isFood` TEXT,
	`isAlcohol` TEXT,
	`metro` TEXT,
    `isAllTime` TEXT,
    `timeOpen` TEXT,
    `timeClose` TEXT,
    `timeWeekendOpen` TEXT,
    `timeWeekendClose` TEXT
);

CREATE TABLE IF NOT EXISTS `favorite_places` (
	`id` INTEGER UNIQUE,
	`name` TEXT,
	`rate` TEXT,
	`currency` TEXT,
	`country` TEXT,
	`address` TEXT,
	`metro` TEXT,
	`logo` TEXT,
	`main_image` TEXT,
	`latitude` DOUBLE,
	`longitude` DOUBLE
);
