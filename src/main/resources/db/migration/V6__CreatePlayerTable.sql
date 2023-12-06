-- Create Player Table
CREATE TABLE player (
  "id" SERIAL PRIMARY KEY,
  "name" VARCHAR(64) NOT NULL,
  "team_id" INT NOT NULL,
  "dob" DATE,
  "phone" VARCHAR(64) CHECK ( LENGTH("phone") BETWEEN 10 AND 11),
  "created_at" TIMESTAMP,
  "updated_at" TIMESTAMP,
  CONSTRAINT fk_player_team FOREIGN KEY ("team_id") REFERENCES "team" ("id") ON DELETE CASCADE
);