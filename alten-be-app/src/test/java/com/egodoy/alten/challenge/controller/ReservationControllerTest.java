package com.egodoy.alten.challenge.controller;

import com.egodoy.alten.challenge.exception.AltenChallengeApiError;
import com.egodoy.alten.challenge.exception.AltenChallengeException;
import com.egodoy.alten.challenge.model.dto.*;
import com.egodoy.alten.challenge.model.enums.Bed;
import com.egodoy.alten.challenge.service.serviceImpl.ReservationServiceImpl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(SpringExtension.class)
class ReservationControllerTest {

    @InjectMocks
    private ReservationController reservationController;

    @Mock
    private ReservationServiceImpl reservationService;

    @Test
    void book() throws AltenChallengeException {
        BookingDTO bookingDTO = BookingDTO.builder()
                .reservation(ReservationDTO.builder()
                        .visitorPhone("0983921382")
                        .visitorEmail("valeska.salazar6@gmail.com")
                        .visitorName("Valeska Salazar")
                        .status((short) 1)
                        .build()
                )
                .rooms(List.of(ReservationRoomDTO.builder()
                        .id(6L)
                        .room(RoomDTO.builder()
                                .id(12L)
                                .roomView("Sea View")
                                .roomType(RoomTypeDTO.builder()
                                        .id(2L)
                                        .name("Senior")
                                        .beds(List.of(RoomTypeBedDTO.builder()
                                                                .bedType(Bed.QUEEN)
                                                                .quantity((short)1)
                                                                .build(),
                                                        RoomTypeBedDTO.builder()
                                                                .bedType(Bed.SINGLE)
                                                                .quantity((short)1)
                                                                .build()
                                                )
                                        )
                                        .build()
                                )
                                .build()
                        )
                        .stayFrom(LocalDate.parse("2021-10-28"))
                        .stayTo(LocalDate.parse("2021-10-30"))
                        .status((short)1)
                        .build()
                ))
                .build();
        BDDMockito.when(reservationService.book(bookingDTO)).thenReturn(bookingDTO);

        ResponseEntity<BookingDTO> response = reservationController.book(bookingDTO);
        Assertions.assertEquals(bookingDTO.getReservation().getId(), response.getBody().getReservation().getId());
        Assertions.assertEquals(bookingDTO.getRooms().size(), response.getBody().getRooms().size());
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void bookException() throws AltenChallengeException {
        BookingDTO bookingDTO = BookingDTO.builder()
                .reservation(ReservationDTO.builder()
                        .visitorPhone("0983921382")
                        .visitorEmail("valeska.salazar6@gmail.com")
                        .visitorName("Valeska Salazar")
                        .status((short) 1)
                        .build()
                )
                .rooms(List.of(ReservationRoomDTO.builder()
                        .id(6L)
                        .room(RoomDTO.builder()
                                .id(12L)
                                .roomView("Sea View")
                                .roomType(RoomTypeDTO.builder()
                                        .id(2L)
                                        .name("Senior")
                                        .beds(List.of(RoomTypeBedDTO.builder()
                                                                .bedType(Bed.QUEEN)
                                                                .quantity((short)1)
                                                                .build(),
                                                        RoomTypeBedDTO.builder()
                                                                .bedType(Bed.SINGLE)
                                                                .quantity((short)1)
                                                                .build()
                                                )
                                        )
                                        .build()
                                )
                                .build()
                        )
                        .stayFrom(LocalDate.parse("2021-10-28"))
                        .stayTo(LocalDate.parse("2021-10-30"))
                        .status((short)1)
                        .build()
                ))
                .build();
        BDDMockito.willThrow(AltenChallengeException.builder().apiError(AltenChallengeApiError.builder().status(HttpStatus.BAD_REQUEST).build()).build())
                .given(reservationService).book(bookingDTO);

        AltenChallengeException exception = Assertions.assertThrows(AltenChallengeException.class, () -> {
            ResponseEntity<BookingDTO> response = reservationController.book(bookingDTO);
        });
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getApiError().getStatus());
        Assertions.assertEquals("/reservation/book", exception.getApiError().getEndpoint());
    }

    @Test
    void modifyReservation() throws AltenChallengeException {
        BookingDTO bookingDTO = BookingDTO.builder()
                .reservation(ReservationDTO.builder()
                        .visitorPhone("0983921382")
                        .visitorEmail("valeska.salazar6@gmail.com")
                        .visitorName("Valeska Salazar")
                        .status((short) 1)
                        .build()
                )
                .rooms(List.of(ReservationRoomDTO.builder()
                        .id(6L)
                        .room(RoomDTO.builder()
                                .id(12L)
                                .roomView("Sea View")
                                .roomType(RoomTypeDTO.builder()
                                        .id(2L)
                                        .name("Senior")
                                        .beds(List.of(RoomTypeBedDTO.builder()
                                                                .bedType(Bed.QUEEN)
                                                                .quantity((short)1)
                                                                .build(),
                                                        RoomTypeBedDTO.builder()
                                                                .bedType(Bed.SINGLE)
                                                                .quantity((short)1)
                                                                .build()
                                                )
                                        )
                                        .build()
                                )
                                .build()
                        )
                        .stayFrom(LocalDate.parse("2021-10-28"))
                        .stayTo(LocalDate.parse("2021-10-30"))
                        .status((short)1)
                        .build()
                ))
                .build();
        BDDMockito.when(reservationService.modifyReservation(bookingDTO)).thenReturn(bookingDTO);

        ResponseEntity<BookingDTO> response = reservationController.modifyReservation(bookingDTO);
        Assertions.assertEquals(bookingDTO.getReservation().getId(), response.getBody().getReservation().getId());
        Assertions.assertEquals(bookingDTO.getRooms().size(), response.getBody().getRooms().size());
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void modifyException() throws AltenChallengeException {
        BookingDTO bookingDTO = BookingDTO.builder()
                .reservation(ReservationDTO.builder()
                        .visitorPhone("0983921382")
                        .visitorEmail("valeska.salazar6@gmail.com")
                        .visitorName("Valeska Salazar")
                        .status((short) 1)
                        .build()
                )
                .rooms(List.of(ReservationRoomDTO.builder()
                        .id(6L)
                        .room(RoomDTO.builder()
                                .id(12L)
                                .roomView("Sea View")
                                .roomType(RoomTypeDTO.builder()
                                        .id(2L)
                                        .name("Senior")
                                        .beds(List.of(RoomTypeBedDTO.builder()
                                                                .bedType(Bed.QUEEN)
                                                                .quantity((short)1)
                                                                .build(),
                                                        RoomTypeBedDTO.builder()
                                                                .bedType(Bed.SINGLE)
                                                                .quantity((short)1)
                                                                .build()
                                                )
                                        )
                                        .build()
                                )
                                .build()
                        )
                        .stayFrom(LocalDate.parse("2021-10-28"))
                        .stayTo(LocalDate.parse("2021-10-30"))
                        .status((short)1)
                        .build()
                ))
                .build();
        BDDMockito.willThrow(AltenChallengeException.builder().apiError(AltenChallengeApiError.builder().status(HttpStatus.BAD_REQUEST).build()).build())
                .given(reservationService).modifyReservation(bookingDTO);

        AltenChallengeException exception = Assertions.assertThrows(AltenChallengeException.class, () -> {
            ResponseEntity<BookingDTO> response = reservationController.modifyReservation(bookingDTO);
        });
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getApiError().getStatus());
        Assertions.assertEquals("/reservation/modify", exception.getApiError().getEndpoint());
    }

    @Test
    void cancelReservation() throws AltenChallengeException {
        UUID reservationId = UUID.randomUUID();
        BDDMockito.willDoNothing().given(reservationService).cancelReservation(reservationId);
        ResponseEntity<String> response = reservationController.cancelReservation(reservationId);

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("Reservation Cancelled successfully", response.getBody());
    }

    @Test
    void cancelReservationException() throws AltenChallengeException {
        UUID reservationId = UUID.randomUUID();
        BDDMockito.willThrow(AltenChallengeException.builder().apiError(AltenChallengeApiError.builder().status(HttpStatus.BAD_REQUEST).build()).build())
                .given(reservationService).cancelReservation(reservationId);

        AltenChallengeException exception = Assertions.assertThrows(AltenChallengeException.class, () -> {
            ResponseEntity<String> response = reservationController.cancelReservation(reservationId);
        });

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getApiError().getStatus());
        Assertions.assertEquals("/reservation/cancel", exception.getApiError().getEndpoint());
    }

    @Test
    void reservation() throws AltenChallengeException {
        UUID reservationId = UUID.randomUUID();
        BookingDTO bookingDTO = BookingDTO.builder()
                .reservation(ReservationDTO.builder()
                        .id(reservationId)
                        .visitorPhone("0983921382")
                        .visitorEmail("valeska.salazar6@gmail.com")
                        .visitorName("Valeska Salazar")
                        .status((short) 1)
                        .build()
                )
                .rooms(List.of(ReservationRoomDTO.builder()
                        .id(6L)
                        .room(RoomDTO.builder()
                                .id(12L)
                                .roomView("Sea View")
                                .roomType(RoomTypeDTO.builder()
                                        .id(2L)
                                        .name("Senior")
                                        .beds(List.of(RoomTypeBedDTO.builder()
                                                                .bedType(Bed.QUEEN)
                                                                .quantity((short)1)
                                                                .build(),
                                                        RoomTypeBedDTO.builder()
                                                                .bedType(Bed.SINGLE)
                                                                .quantity((short)1)
                                                                .build()
                                                )
                                        )
                                        .build()
                                )
                                .build()
                        )
                        .stayFrom(LocalDate.parse("2021-10-28"))
                        .stayTo(LocalDate.parse("2021-10-30"))
                        .status((short)1)
                        .build()
                ))
                .build();
        BDDMockito.when(reservationService.getReservation(reservationId)).thenReturn(bookingDTO);

        ResponseEntity<BookingDTO> response = reservationController.getReservation(reservationId);
        Assertions.assertEquals(bookingDTO.getReservation().getId(), response.getBody().getReservation().getId());
        Assertions.assertEquals(bookingDTO.getRooms().size(), response.getBody().getRooms().size());
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getReservationException() throws AltenChallengeException {
        UUID reservationId = UUID.randomUUID();
        BDDMockito.willThrow(AltenChallengeException.builder().apiError(AltenChallengeApiError.builder().status(HttpStatus.BAD_REQUEST).build()).build())
                .given(reservationService).getReservation(reservationId);

        AltenChallengeException exception = Assertions.assertThrows(AltenChallengeException.class, () -> {
            ResponseEntity<BookingDTO> response = reservationController.getReservation(reservationId);
        });

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getApiError().getStatus());
        Assertions.assertEquals("/reservation/".concat(reservationId.toString()), exception.getApiError().getEndpoint());
    }
}