-- Create Status Tournament Enum type
CREATE TYPE types AS ENUM ('EVENT', 'MATCH');


ALTER TABLE match
    ADD COLUMN "duration" INT,
    ADD COLUMN "title" VARCHAR(200),
ADD COLUMN "type" types;
