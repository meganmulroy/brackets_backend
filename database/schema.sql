BEGIN TRANSACTION;

DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS tournaments;
DROP TABLE IF EXISTS sports;
DROP TABLE IF EXISTS users_tournament;
DROP TABLE IF EXISTS bracket_matches;
DROP TABLE IF EXISTS matches;
DROP TABLE IF EXISTS users_matches;

CREATE TABLE users (
  user_id serial PRIMARY KEY,
  username varchar(255) NOT NULL UNIQUE,     -- Username
  password varchar(32) NOT NULL,      -- Password
  salt varchar(256) NOT NULL,          -- Password salt
  email varchar(255) NOT NULL       -- Email
  -- role varchar (64)
);

CREATE TABLE sports (
  sport_id serial PRIMARY KEY,
  sport_name varchar(64) NOT NULL UNIQUE     -- Sport name
);

CREATE TABLE tournaments (
  tournament_id serial PRIMARY KEY,
  name varchar(64) NOT NULL UNIQUE,
  description varchar(255) NOT NULL,
  organizer int NOT NULL,
  start_date DATE,
  end_date DATE,
  reg_deadline DATE,
  sport_id int,
  type varchar(64) NOT NULL, 
  participant_max int NOT NULL,
  status varchar(16),
  results int,
  bracket_generated boolean DEFAULT false,

  CONSTRAINT fk_tournaments_sports FOREIGN KEY (sport_id) REFERENCES sports(sport_id)
);

CREATE TABLE users_tournaments (
  user_id int NOT NULL,
  tournament_id int NOT NULL,
  -- organizer boolean NOT NULL DEFAULT 'false',
  response varchar(32) NOT NULL DEFAULT 'PENDING',

  CONSTRAINT fk_users_tournaments_users FOREIGN KEY (user_id) REFERENCES users(user_id),
  CONSTRAINT fk_users_tournaments_tournaments FOREIGN KEY (tournament_id) REFERENCES tournaments(tournament_id)
);

CREATE TABLE matches (
  match_id serial PRIMARY KEY,
  tournament_id int NOT NULL,
  game_id int NOT NULL,
  round int NOT NULL,
  completed boolean DEFAULT false,

  CONSTRAINT fk_matches_tournaments FOREIGN KEY (tournament_id) REFERENCES tournaments(tournament_id)
);

  CREATE TABLE users_matches (
  match_id int NOT NULL,
  user_id int,
  winner boolean,
  top_slot boolean,
  
  CONSTRAINT fk_users_matches_matches FOREIGN KEY (match_id) REFERENCES matches(match_id),
  CONSTRAINT fk_users_matches_users FOREIGN KEY (user_id) REFERENCES users(user_id)
);

COMMIT TRANSACTION;
