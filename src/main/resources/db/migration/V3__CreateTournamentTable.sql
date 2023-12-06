-- Create Status Tournament Enum type
CREATE TYPE status_tournament AS ENUM ('NEED_INFORMATION', 'READY', 'IN_PROGRESS', 'FINISHED', 'DISCARDED', 'DELETED');
-- cast status_tournament Enum to character varying
CREATE CAST (character varying AS status_tournament) WITH INOUT AS IMPLICIT;

-- Create Format in Tournament Enum type
CREATE TYPE tournament_format AS ENUM ('DIRECT_ELIMINATION', 'ROUND_ROBIN');
-- cast tournament_format Enum to character varying
CREATE CAST (character varying AS tournament_format) WITH INOUT AS IMPLICIT;

-- Create tournament table
CREATE TABLE tournament (
    "tournament_id" SERIAL PRIMARY KEY,
    "title" VARCHAR(255) NOT NULL CHECK (LENGTH("title") BETWEEN 2 AND 50),
    "category_id" INT NOT NULL,
    "created_at" TIMESTAMP,
    "updated_at" TIMESTAMP,
    "status" status_tournament,
    "match_duration" INT NOT NULL,
    "format" tournament_format,
    "is_deleted" BOOLEAN DEFAULT FALSE,
    "deleted_at" TIMESTAMP,
    CONSTRAINT fk_tournament_category FOREIGN KEY ("category_id") REFERENCES "category" ("category_id")
);

-- Create organizerTournament table
CREATE TABLE organizer_tournament (
    "id" SERIAL PRIMARY KEY,
    "user_id" INT NOT NULL,
    "tournament_id" INT NOT NULL,
    CONSTRAINT fk_user FOREIGN KEY ("user_id") REFERENCES "users" ("id"),
    CONSTRAINT fk_tournament FOREIGN KEY ("tournament_id") REFERENCES tournament ("tournament_id")
);