CREATE TABLE public.at_reservation (
	id SERIAL PRIMARY KEY,
	visitor_phone text NULL,
	visitor_email text NULL,
	status INT NULL,
	visitor_name text NOT NULL,
	created_at timestamp NULL,
	updated_at timestamp NULL,
	operation varchar NULL,
	reservation_id uuid NULL
);