CREATE TABLE IF NOT EXISTS public.reservation (
	id uuid PRIMARY KEY,
	status int NULL,
	visitor_name text NOT NULL,
	visitor_email text NULL,
	visitor_phone text NULL,
	created_at timestamp NULL,
	updated_at timestamp NULL
);