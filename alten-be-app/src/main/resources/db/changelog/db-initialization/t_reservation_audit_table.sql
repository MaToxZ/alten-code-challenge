CREATE TRIGGER t_reservation_audit_table
 AFTER INSERT OR UPDATE OR DELETE ON public.reservation
 FOR EACH ROW EXECUTE PROCEDURE public.reservtation_audit_function();