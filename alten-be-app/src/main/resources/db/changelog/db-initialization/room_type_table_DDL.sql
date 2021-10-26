CREATE TABLE IF NOT EXISTS public.room_type (
	id serial PRIMARY KEY,
	"name" text NOT NULL,
	occupants int NULL
);