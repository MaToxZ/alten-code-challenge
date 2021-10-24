INSERT INTO public.room_type (id,"name",occupants) VALUES
	 (1,'Junior',1),
	 (2,'Senior',3),
	 (3,'Executive',2),
	 (4,'Family',5),
	 (5,'Presidential',8);
INSERT INTO public.room_type_bed (room_type_id,bed_type,quantity) VALUES
	 (1,'SINGLE',1),
	 (2,'QUEEN',1),
	 (2,'SINGLE',1),
	 (3,'KING',1),
	 (4,'QUEEN',1),
	 (4,'TWIN',1),
	 (4,'SINGLE',1),
	 (5,'KING',1),
	 (5,'QUEEN',1),
	 (5,'TWIN',2);

INSERT INTO public.room (room_type_id,room_view,status) VALUES
	 (1,'Sea View',0),
	 (1,'Sea View',0),
	 (1,'Sea View',0),
	 (1,'Sea View',0),
	 (1,'Pool View',0),
	 (1,'Pool View',0),
	 (1,'Pool View',0),
	 (1,'Pool View',0),
	 (2,'Sea View',0),
	 (2,'Sea View',0);
INSERT INTO public.room (room_type_id,room_view,status) VALUES
	 (2,'Sea View',0),
	 (2,'Sea View',1),
	 (2,'Pool View',0),
	 (3,'Sea View',0),
	 (3,'Sea View',0),
	 (3,'Sea View',0),
	 (4,'Pool View',0),
	 (4,'Pool View',0),
	 (4,'Pool View',0),
	 (5,'Sea View',0);
INSERT INTO public.room (room_type_id,room_view,status) VALUES
	 (5,'Sea View',0);

INSERT INTO public.reservation (id,status,visitor_name,visitor_email,visitor_phone,created_at,updated_at) VALUES
	 ('c1a9687c-a6d1-4826-92c8-da3139494cc3',1,'Eladio Godoy','eladiogodoy328@gmail.com','0983921382','2021-10-23 02:06:55.69087','2021-10-23 02:06:55.690907'),
	 ('fb804a49-d46c-43bc-b109-6cf1ec1416e4',1,'Valeska Salazar','valeska.salazar6@gmail.com','0983921382','2021-10-23 03:45:19.791818','2021-10-23 03:45:19.791861');
INSERT INTO public.reservation_room (room_id,reservation_id,stay_from,stay_to,status,created_at,updated_at) VALUES
	 (12,'fb804a49-d46c-43bc-b109-6cf1ec1416e4','2021-10-28 00:00:00','2021-10-30 23:59:59',1,'2021-10-23 03:45:23.655377','2021-10-23 03:45:23.655413');