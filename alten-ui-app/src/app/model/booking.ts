import { Reservation } from "./reservation";
import { ReservationRoom } from "./reservationRoom";
import { Room } from "./room";

export interface Booking{
    reservation?: Reservation;
    rooms: ReservationRoom[];
}