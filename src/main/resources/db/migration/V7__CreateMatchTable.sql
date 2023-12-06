-- Create Match Table
CREATE TABLE match (
  "id" SERIAL PRIMARY KEY,
  "team_one_id" INT,
  "team_two_id" INT,
  "team_one_result" INT,
  "team_two_result" INT,
  "start_time" TIME,
  "end_time" TIME,
  "event_date_id" INT,
  CONSTRAINT fk_match_event_date FOREIGN KEY ("event_date_id") REFERENCES "event_date" ("id")
);