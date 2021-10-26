CREATE TABLE public.at_reservation_room (
	id SERIAL PRIMARY KEY,
	reservation_room_id INT,
	reservation_id uuid NULL,
    room_id int,
    stay_from TIMESTAMP,
    stay_to TIMESTAMP,
	status INT NULL,
	created_at timestamp NULL,
	updated_at timestamp NULL,
	operation varchar NULL
);