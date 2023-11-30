
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'user_role') THEN
CREATE TYPE user_role AS ENUM ('ADMIN', 'ORGANIZER');
END IF;
END $$;


CREATE TABLE IF NOT EXISTS "users" (
    "id" SERIAL PRIMARY KEY,
    "email" VARCHAR(255) UNIQUE NOT NULL CHECK (LENGTH("email") BETWEEN 3 AND 50),
    "password" VARCHAR(255) NOT NULL CHECK (LENGTH("password") > 0),
    "first_name" VARCHAR(64) NOT NULL CHECK (LENGTH("first_name") > 0),
    "last_name" VARCHAR(64) NOT NULL CHECK (LENGTH("last_name") > 0),
    "phone_number" VARCHAR(64) NOT NULL CHECK (LENGTH("phone_number") BETWEEN 10 AND 11),
    "created_at" TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    "role" user_role,
    "is_deleted" BOOLEAN DEFAULT FALSE,
    "deleted_at" TIMESTAMP
    );


-- Enum Postgres cannot automatic convert into Enum Java type, need cast user_role Enum to character varying
DO $$
BEGIN
  IF EXISTS (SELECT 1 FROM pg_cast WHERE castsource = 'character varying'::regtype AND casttarget = 'user_role'::regtype) THEN
DROP CAST (character varying AS user_role);
END IF;

CREATE CAST (character varying AS user_role) WITH INOUT AS IMPLICIT;
END $$;
