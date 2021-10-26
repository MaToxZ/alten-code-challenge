import { ReservationRoom } from "./reservationRoom";

export interface ReservationRoomEvent{
    action: string;
    reservationRoom: ReservationRoom
}