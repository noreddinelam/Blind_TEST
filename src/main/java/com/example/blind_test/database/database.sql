-- Warning: You can generate script only for one table/view at a time in your Free plan
--
-- ****************** SqlDBM: MySQL ******************;
-- ***************************************************;

use blind_test;

-- ************************************** `Question`

CREATE TABLE `Question`
(
    `id`           integer NOT NULL ,
    `id_partie`    integer NOT NULL ,
    `id_ressource` varchar(45) NOT NULL ,
    `ordre`        integer NOT NULL ,

    PRIMARY KEY (`id`),
    KEY `FK_28` (`id_partie`),
    CONSTRAINT `FK_26` FOREIGN KEY `FK_28` (`id_partie`) REFERENCES `Partie` (`id`)
);

-- ************************************** `Partie`

CREATE TABLE `Partie`
(
    `id`               integer NOT NULL ,
    `type`             bit NOT NULL ,
    `current_question` integer NOT NULL ,
    `rounds`           integer NOT NULL ,
    `players`          integer NOT NULL ,
    `etat`             bit NOT NULL ,

    PRIMARY KEY (`id`),
    KEY `FK_48` (`current_question`),
    CONSTRAINT `FK_46` FOREIGN KEY `FK_48` (`current_question`) REFERENCES `Question` (`id`)
);

-- ************************************** `Joueur`

CREATE TABLE `Joueur`
(
    `username`  varchar(45) NOT NULL ,
    `id_partie` integer NOT NULL ,
    `score`     integer NOT NULL ,

    PRIMARY KEY (`username`),
    KEY `FK_44` (`id_partie`),
    CONSTRAINT `FK_42` FOREIGN KEY `FK_44` (`id_partie`) REFERENCES `Partie` (`id`)
);
