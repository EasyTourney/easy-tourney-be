-- Create EventDate Table
CREATE TABLE event_date (
    "id" SERIAL PRIMARY KEY,
    "tournament_id" INT NOT NULL,
    "start_at" TIMESTAMP NOT NULL,
    "end_at" TIMESTAMP NOT NULL CHECK ( end_at > start_at),
    "created_at" TIMESTAMP,
    "updated_at" TIMESTAMP,
    CONSTRAINT fk_tournament_id FOREIGN KEY ("tournament_id") REFERENCES "tournament" ("tournament_id")
);