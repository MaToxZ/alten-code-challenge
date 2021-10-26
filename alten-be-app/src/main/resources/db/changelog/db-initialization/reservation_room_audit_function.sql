CREATE OR REPLACE FUNCTION public.reservtation_room_audit_function() RETURNS trigger AS $body$
DECLARE
BEGIN

    if (TG_OP = 'UPDATE') then
        INSERT INTO public.at_reservation_room
					(reservation_room_id,reservation_id, room_id, stay_from, stay_to, status, created_at, operation)
			VALUES(NEW.id,NEW.reservation_id,NEW.room_id,NEW.stay_from,NEW.stay_to, NEW.status, NEW.created_at, 'UPDATE');
        RETURN NEW;
	elseif (TG_OP = 'DELETE') then
   		INSERT INTO public.at_reservation_room
					(reservation_room_id,reservation_id, room_id, stay_from, stay_to, status, created_at, operation)
			VALUES(OLD.id,OLD.reservation_id,OLD.room_id,OLD.stay_from,OLD.stay_to, OLD.status, OLD.created_at, 'DELETE');
        RETURN NEW;
   	elseif (TG_OP = 'INSERT') then
   		INSERT INTO public.at_reservation_room
					(reservation_room_id,reservation_id, room_id, stay_from, stay_to, status, created_at, operation)
			VALUES(NEW.id,NEW.reservation_id,NEW.room_id,NEW.stay_from,NEW.stay_to, NEW.status, NEW.created_at, 'INSERT');
        RETURN NEW;
    else
        RAISE WARNING '[PUBLIC.RESERVATION_ROOM_AUDIT_FUNCTION] - Other action occurred: %, at %',TG_OP,now();
        RETURN NULL;
    end if;

EXCEPTION
    WHEN data_exception THEN
        RAISE WARNING '[PUBLIC.RESERVATION_ROOM_AUDIT_FUNCTION]  - UDF ERROR [DATA EXCEPTION] - SQLSTATE: %, SQLERRM: %',SQLSTATE,SQLERRM;
        RETURN NULL;
    WHEN unique_violation THEN
        RAISE WARNING '[PUBLIC.RESERVATION_ROOM_AUDIT_FUNCTION]  - UDF ERROR [UNIQUE] - SQLSTATE: %, SQLERRM: %',SQLSTATE,SQLERRM;
        RETURN NULL;
    WHEN others THEN
        RAISE WARNING '[PUBLIC.RESERVATION_ROOM_AUDIT_FUNCTION]  - UDF ERROR [OTHER] - SQLSTATE: %, SQLERRM: %',SQLSTATE,SQLERRM;
        RETURN NULL;
END;
$body$
LANGUAGE plpgsql;