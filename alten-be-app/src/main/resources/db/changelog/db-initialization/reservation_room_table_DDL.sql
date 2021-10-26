CREATE TABLE IF NOT EXISTS public.reservation_room (
        id SERIAL PRIMARY KEY,
        room_id INT REFERENCES public.room(id),
        reservation_id UUID references public.reservation(id),
        stay_from timestamp NULL,
        stay_to timestamp NULL,
        status INT NULL,
        created_at timestamp NULL,
        updated_at timestamp NULL
);
