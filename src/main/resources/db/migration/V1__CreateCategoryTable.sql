CREATE TABLE IF NOT EXISTS category (
    category_id bigint GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    category_name varchar(30) NOT NULL, CHECK (length(category_name) BETWEEN 1 AND 30),
    created_at timestamptz,
    updated_at timestamptz,
    deleted_at timestamptz,
    is_deleted boolean DEFAULT FALSE
);