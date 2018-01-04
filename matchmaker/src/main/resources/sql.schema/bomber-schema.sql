/*
BEGIN ;

DROP SCHEMA IF EXISTS bomber CASCADE ;
CREATE SCHEMA bomber;


DROP TABLE IF EXISTS bomber.gs;
CREATE TABLE bomber.gs (
  game  INTEGER    NOT NULL,
  time   TIMESTAMP    NOT NULL,
  status  BOOLEAN  DEFAULT (FALSE),

  PRIMARY KEY (game)
);

DROP TABLE IF EXISTS bomber.user;
CREATE TABLE bomber.user (
  game_id    INTEGER   NOT NULL,
  id INTEGER PRIMARY KEY,
  name VARCHAR(25)  NOT NULL,

  FOREIGN KEY (game_id) REFERENCES bomber.gs (game) ON DELETE CASCADE
);


COMMIT;*/
