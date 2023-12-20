ALTER TABLE event_date
DROP COLUMN start_at,
DROP COLUMN end_at;


ALTER TABLE event_date
ADD COLUMN date date,
ADD COLUMN start_time time,
ADD COLUMN end_time time;
