-- Warning: You can generate script only for one table/view at a time in your Free plan
--
-- ****************** SqlDBM: MySQL ******************;
-- ***************************************************;

use blind_test;

-- ************************************** `Question`

CREATE TABLE `Question`
(
    `id`           integer NOT NULL auto_increment,
    `id_resource` varchar(255) NOT NULL ,
    `response`   varchar(255) NOT NULL ,
    `choice1`    varchar(255) NOT NULL ,
    `choice2`    varchar(255) NOT NULL ,
    `choice3`    varchar(255) NOT NULL ,
    `type`        bit NOT NULL,
    PRIMARY KEY (`id`)
);

-- ************************************** `Game`

CREATE TABLE `Game`
(
    `id`               integer NOT NULL auto_increment,
    `type`             binary NOT NULL ,
    `rounds`           integer NOT NULL ,
    `remainedPlayers`  integer NOT NULL ,
    `totalPlayers`     integer NOT NULL ,
    `timeQuestion`     integer NOT NULL ,
    `state`             bit NOT NULL ,

    PRIMARY KEY (`id`)
);

-- ************************************** `Question_Game`

CREATE TABLE `Question_Game`
(
    `id_question`  integer NOT NULL ,
    `id_game`    integer NOT NULL ,
    `orderQuestion`        integer NOT NULL ,
    `state`         bit,

    KEY `FK_30` (`id_question`),
    CONSTRAINT `FK_32` FOREIGN KEY `FK_30` (`id_question`) REFERENCES `Question` (`id`),
    KEY `FK_28` (`id_game`),
    CONSTRAINT `FK_26` FOREIGN KEY `FK_28` (`id_game`) REFERENCES `Game` (`id`) ON DELETE CASCADE
);

-- ************************************** `Player`

CREATE TABLE `Player`
(
    `username`  varchar(45) NOT NULL ,
    `id_game` integer NOT NULL ,
    `score`     integer NOT NULL ,

    KEY `FK_44` (`id_game`),
    CONSTRAINT `FK_42` FOREIGN KEY `FK_44` (`id_game`) REFERENCES `Game` (`id`) ON DELETE CASCADE ,
    CONSTRAINT `UNIQUE_CONSTRAINT` UNIQUE (`username`,`id_game`)

);
