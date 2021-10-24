CREATE TABLE IF NOT EXISTS public.room_type_bed (
	id SERIAL PRIMARY KEY,
	room_type_id INT REFERENCES public.room_type(id),
	bed_type text NOT NULL,
	quantity INT NULL
);