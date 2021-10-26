import { Room } from "./room";

export interface ReservationRoom{
    id?: number;
    room: Room;
    stayFrom?: string;
    stayTo?: string;
    status?: number;
}