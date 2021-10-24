package com.egodoy.alten.challenge.utils;

import com.egodoy.alten.challenge.model.*;
import com.egodoy.alten.challenge.model.dto.*;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Util Class containing the mapping methods to manage the different entities.
 */
public class Mapper {

    private Mapper(){

    }
    public static Room roomDtoToRoom(RoomDTO roomDTO){
        if(Objects.nonNull(roomDTO)){
            return Room.builder()
                    .roomView(roomDTO.getRoomView())
                    .roomType(Mapper.roomTypeDtoToRoomType(roomDTO.getRoomType()))
                    .id(roomDTO.getId())
                    .build();
        }
        return null;
    }

    public static RoomDTO roomDtoToRoom(Room room){
        if(Objects.nonNull(room)){
            return RoomDTO.builder()
                    .roomView(room.getRoomView())
                    .roomType(Mapper.roomTypeToRoomTypeDTO(room.getRoomType()))
                    .id(room.getId())
                    .build();
        }
        return null;
    }

    public static RoomType roomTypeDtoToRoomType(RoomTypeDTO roomTypeDTO){
        if(Objects.nonNull(roomTypeDTO)){
            return RoomType.builder()
                .beds(roomTypeDTO.getBeds().stream()
                        .map(Mapper::roomTypeBedDtoToRoomTypeBed)
                        .collect(Collectors.toList())
                )
                .id(roomTypeDTO.getId())
                .name(roomTypeDTO.getName())
                .occupants(roomTypeDTO.getOccupants())
                .build();
        }
        return null;
    }

    public static RoomTypeBed roomTypeBedDtoToRoomTypeBed(RoomTypeBedDTO roomTypeBedDTO){
        if(Objects.nonNull(roomTypeBedDTO)){
            return RoomTypeBed.builder()
                    .bedType(roomTypeBedDTO.getBedType())
                    .id(roomTypeBedDTO.getId())
                    .quantity(roomTypeBedDTO.getQuantity())
                    .build();
        }
        return null;
    }

    public static RoomTypeDTO roomTypeToRoomTypeDTO(RoomType roomType){
        if(Objects.nonNull(roomType)){
            return RoomTypeDTO.builder()
                    .beds(roomType.getBeds().stream()
                            .map(Mapper::roomTypeBedToRoomTypeBedDto)
                            .collect(Collectors.toList())
                    )
                    .name(roomType.getName())
                    .occupants(roomType.getOccupants())
                    .id(roomType.getId())
                    .build();
        }
        return null;
    }

    public static RoomTypeBedDTO roomTypeBedToRoomTypeBedDto(RoomTypeBed roomTypeBed){
        if(Objects.nonNull(roomTypeBed)){
            return RoomTypeBedDTO
                    .builder()
                    .bedType(roomTypeBed.getBedType())
                    .quantity(roomTypeBed.getQuantity())
                    .build();
        }
        return null;
    }

    public static List<ReservationRoom> createReservationRoomsFromDto(List<ReservationRoomDTO> reservationRoomDTOS, Reservation reservation){
        if(Objects.nonNull(reservationRoomDTOS) && !reservationRoomDTOS.isEmpty()) {
            return reservationRoomDTOS.stream()
                    .map(reservationRoomDTO -> Mapper.reservationRoomDtoToEntity(reservationRoomDTO,reservation))
                    .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public static ReservationRoom reservationRoomDtoToEntity(ReservationRoomDTO reservationRoomDTO, Reservation reservation){
        if(Objects.nonNull(reservationRoomDTO)){
            return ReservationRoom.builder()
                    .reservation(Reservation.builder()
                            .id(reservation.getId())
                            .build()
                            )
                    .room(Mapper.roomDtoToRoom(reservationRoomDTO.getRoom()))
                    .status(Objects.nonNull(reservationRoomDTO.getStatus()) ? reservationRoomDTO.getStatus() : (short) 1)
                    .id(reservationRoomDTO.getId())
                    .stayFrom(reservationRoomDTO.getStayFrom().atTime(0,0,0))
                    .stayTo(reservationRoomDTO.getStayTo().atTime(23,59,59))
                    .build();
        }
        return null;
    }

    public static ReservationRoomDTO reservationRoomToDto(ReservationRoom reservationRoom){
        if(Objects.nonNull(reservationRoom)){
            return ReservationRoomDTO.builder()
                    .room(Mapper.roomDtoToRoom(reservationRoom.getRoom()))
                    .status(Objects.nonNull(reservationRoom.getStatus()) ? reservationRoom.getStatus() : (short) 1)
                    .id(reservationRoom.getId())
                    .stayFrom(reservationRoom.getStayFrom().toLocalDate())
                    .stayTo(reservationRoom.getStayTo().toLocalDate())
                    .build();
        }
        return null;
    }

    public static Reservation reservationDtoToEntity(ReservationDTO reservationDTO){
        if (Objects.nonNull(reservationDTO)){
            return Reservation.builder()
                    .status(reservationDTO.getStatus())
                    .visitorPhone(reservationDTO.getVisitorPhone())
                    .visitorEmail(reservationDTO.getVisitorEmail())
                    .visitorName(reservationDTO.getVisitorName())
                    .id(reservationDTO.getId())
                    .build();
        }
        return null;
    }

    public static BookingDTO reservationToBookingDTO(Reservation reservation){
        if(Objects.nonNull(reservation)){
            return BookingDTO.builder()
                    .reservation(ReservationDTO.builder()
                            .id(reservation.getId())
                            .status(reservation.getStatus())
                            .visitorEmail(reservation.getVisitorEmail())
                            .visitorName(reservation.getVisitorName())
                            .visitorPhone(reservation.getVisitorPhone())
                            .build()
                    )
                    .rooms(reservation.getReservationRooms().stream()
                            .map(Mapper::reservationRoomToDto)
                            .collect(Collectors.toList())
                    )
                    .build();
        }
        return null;
    }

    public static BookingDTO reservationRoomsToBookingDto(List<ReservationRoom> reservedRooms){
        if(Objects.nonNull(reservedRooms) && !reservedRooms.isEmpty()){
            List<ReservationRoomDTO> rooms = reservedRooms.stream()
                    .map(Mapper::reservationRoomToDto)
                    .collect(Collectors.toList());
            return BookingDTO.builder()
                    .reservation(ReservationDTO.builder()
                            .visitorName(reservedRooms.get(0).getReservation().getVisitorName())
                            .visitorEmail(reservedRooms.get(0).getReservation().getVisitorEmail())
                            .visitorPhone(reservedRooms.get(0).getReservation().getVisitorPhone())
                            .id(reservedRooms.get(0).getReservation().getId())
                            .status(reservedRooms.get(0).getReservation().getStatus())
                            .build()
                    )
                    .rooms(rooms)
                    .build();
        }
        return null;
    }

}
