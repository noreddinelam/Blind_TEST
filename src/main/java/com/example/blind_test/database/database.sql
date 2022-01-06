-- Warning: You can generate script only for one table/view at a time in your Free plan
--
-- ****************** SqlDBM: MySQL ******************;
-- ***************************************************;

use blind_test;

-- ************************************** `Question`

CREATE TABLE `Question`
(
    `id`           integer NOT NULL ,
    `id_game`    integer NOT NULL ,
    `id_resource` varchar(45) NOT NULL ,
    `order`        integer NOT NULL ,

    PRIMARY KEY (`id`),
    KEY `FK_28` (`id_game`),
    CONSTRAINT `FK_26` FOREIGN KEY `FK_28` (`id_game`) REFERENCES `Game` (`id`)
);

-- ************************************** `Game`

CREATE TABLE `Game`
(
    `id`               integer NOT NULL ,
    `type`             bit NOT NULL ,
    `current_question` integer NOT NULL ,
    `rounds`           integer NOT NULL ,
    `players`          integer NOT NULL ,
    `state`             bit NOT NULL ,

    PRIMARY KEY (`id`),
    KEY `FK_48` (`current_question`),
    CONSTRAINT `FK_46` FOREIGN KEY `FK_48` (`current_question`) REFERENCES `Question` (`id`)
);

-- ************************************** `Player`

CREATE TABLE `Player`
(
    `username`  varchar(45) NOT NULL ,
    `id_game` integer NOT NULL ,
    `score`     integer NOT NULL ,

    KEY `FK_44` (`id_game`),
    CONSTRAINT `FK_42` FOREIGN KEY `FK_44` (`id_game`) REFERENCES `Game` (`id`) ,
    CONSTRAINT `UNIQUE_CONSTRAINT` UNIQUE (`username`,`id_game`)

);
