ALTER TABLE tournament
    ADD COLUMN "start_time_default" time,
    ADD COLUMN "end_time_default" time,
    ALTER COLUMN description TYPE VARCHAR(200);
