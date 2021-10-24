CREATE TABLE IF NOT EXISTS public.room (
	id SERIAL PRIMARY KEY,
	room_type_id INT REFERENCES public.room_type(id),
	room_view text NOT NULL,
	status INT NULL
);