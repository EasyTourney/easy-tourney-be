-- Create Team Table
CREATE TABLE team (
    "id" SERIAL PRIMARY KEY,
    "name" VARCHAR(255),
    "tournament_id" INT NOT NULL,
    "score" INT DEFAULT 0,
    "created_at" TIMESTAMP,
    "updated_at" TIMESTAMP,
    CONSTRAINT fk_team_tournament FOREIGN KEY ("tournament_id") REFERENCES "tournament" ("tournament_id")
);