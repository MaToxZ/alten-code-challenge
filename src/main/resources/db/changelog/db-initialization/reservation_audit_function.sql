CREATE OR REPLACE FUNCTION public.reservtation_audit_function() RETURNS trigger AS $body$
DECLARE
BEGIN

    if (TG_OP = 'UPDATE') then
        insert into public.at_reservation (reservation_id,status,visitor_name,visitor_phone,visitor_email,created_at,operation)
        values (NEW.id,NEW.status,NEW.visitor_name,NEW.visitor_phone, NEW.visitor_email, NEW.created_at, 'UPDATE');
        RETURN NEW;
	elseif (TG_OP = 'DELETE') then
   		insert into public.at_reservation (reservation_id,status,visitor_name,visitor_phone,visitor_email,created_at,operation)
         values (OLD.id,OLD.status,OLD.visitor_name,OLD.visitor_phone, OLD.visitor_email, OLD.created_at, 'DELETE');
        RETURN NEW;
   	elseif (TG_OP = 'INSERT') then
   		insert into public.at_reservation (reservation_id,status,visitor_name,visitor_phone,visitor_email,created_at,operation)
         values (NEW.id,NEW.status,NEW.visitor_name,NEW.visitor_phone, NEW.visitor_email, NEW.created_at, 'INSERT');
        RETURN NEW;
    else
        RAISE WARNING '[PUBLIC.RESERVATION_AUDIT_FUNCTION] - Other action occurred: %, at %',TG_OP,now();
        RETURN NULL;
    end if;

EXCEPTION
    WHEN data_exception THEN
        RAISE WARNING '[PUBLIC.RESERVATION_AUDIT_FUNCTION]  - UDF ERROR [DATA EXCEPTION] - SQLSTATE: %, SQLERRM: %',SQLSTATE,SQLERRM;
        RETURN NULL;
    WHEN unique_violation THEN
        RAISE WARNING '[PUBLIC.RESERVATION_AUDIT_FUNCTION]  - UDF ERROR [UNIQUE] - SQLSTATE: %, SQLERRM: %',SQLSTATE,SQLERRM;
        RETURN NULL;
    WHEN others THEN
        RAISE WARNING '[PUBLIC.RESERVATION_AUDIT_FUNCTION]  - UDF ERROR [OTHER] - SQLSTATE: %, SQLERRM: %',SQLSTATE,SQLERRM;
        RETURN NULL;
END;
$body$
LANGUAGE plpgsql;