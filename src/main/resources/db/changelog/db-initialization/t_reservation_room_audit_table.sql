CREATE TRIGGER t_reservation_room_audit_table
 AFTER INSERT OR UPDATE OR DELETE ON public.reservation_room
 FOR EACH ROW EXECUTE PROCEDURE public.reservtation_room_audit_function();