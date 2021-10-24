package com.egodoy.alten.challenge.service.serviceImpl;

import com.egodoy.alten.challenge.exception.AltenChallengeException;
import com.egodoy.alten.challenge.model.*;
import com.egodoy.alten.challenge.model.dto.*;
import com.egodoy.alten.challenge.model.enums.Bed;
import com.egodoy.alten.challenge.repository.ReservationRepository;
import com.egodoy.alten.challenge.repository.ReservationRoomRepository;
import com.egodoy.alten.challenge.utils.Mapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@ExtendWith(SpringExtension.class)
class ReservationServiceImplTest {

    @InjectMocks
    private ReservationServiceImpl reservationService;

    @Mock
    private ReservationRepository repository;

    @Mock
    private ReservationRoomRepository reservationRoomRepository;

    @Test
    void bookSuccess() throws AltenChallengeException {
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
                                .stayFrom(LocalDate.now().plusDays(1))
                                .stayTo(LocalDate.now().plusDays(3))
                                .status((short)1)
                                .build()
                        ))
                        .build();
        Reservation reservation = Mapper.reservationDtoToEntity(bookingDTO.getReservation());
        reservation.setId(UUID.randomUUID());
        List<ReservationRoom> reservationRooms = Mapper.createReservationRoomsFromDto(bookingDTO.getRooms(), reservation);
        BDDMockito.when(repository.saveAndFlush(BDDMockito.any())).thenReturn(reservation);
        BDDMockito.when(reservationRoomRepository.findByRoomsAndDates(BDDMockito.any(),BDDMockito.any(), BDDMockito.any())).thenReturn(Collections.emptyList());
        BDDMockito.when(reservationRoomRepository.saveAll(BDDMockito.any())).thenReturn(reservationRooms);

        BookingDTO response = reservationService.book(bookingDTO);

        Assertions.assertEquals(reservation.getId(),response.getReservation().getId());
        Assertions.assertEquals(reservationRooms.size(), response.getRooms().size());
    }

    @Test
    void bookRoomNullException() throws AltenChallengeException {

        AltenChallengeException exception = Assertions.assertThrows(AltenChallengeException.class, () -> {
            BookingDTO response = reservationService.book(null);
        });

        Assertions.assertEquals("There was an issue in the request",exception.getApiError().getMessage());
        Assertions.assertEquals("No reservation object provided",exception.getApiError().getDebugMessage());

    }

    @Test
    void bookRoomsNullException() throws AltenChallengeException {
        BookingDTO bookingDTO = BookingDTO.builder()
                .reservation(ReservationDTO.builder()
                        .visitorPhone("0983921382")
                        .visitorEmail("valeska.salazar6@gmail.com")
                        .visitorName("Valeska Salazar")
                        .status((short) 1)
                        .build()
                )
                .rooms(Collections.emptyList())
                .build();
        AltenChallengeException exception = Assertions.assertThrows(AltenChallengeException.class, () -> {
            BookingDTO response = reservationService.book(bookingDTO);
        });

        Assertions.assertEquals("There was an issue in the request",exception.getApiError().getMessage());
        Assertions.assertEquals("You need to provide at least a room to book",exception.getApiError().getDebugMessage());

    }

    @Test
    void bookRoomIdNullException() throws AltenChallengeException {
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
                        .stayFrom(LocalDate.now().plusDays(1))
                        .stayTo(LocalDate.now().plusDays(3))
                        .status((short)1)
                        .build()
                ))
                .build();
        AltenChallengeException exception = Assertions.assertThrows(AltenChallengeException.class, () -> {
            BookingDTO response = reservationService.book(bookingDTO);
        });

        Assertions.assertEquals("There was an issue in the request",exception.getApiError().getMessage());
        Assertions.assertEquals("Some of the provided rooms are invalid",exception.getApiError().getDebugMessage());

    }

    @Test
    void bookFromIsEqualTodayErrorException() throws AltenChallengeException {
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
                        .stayFrom(LocalDate.now())
                        .stayTo(LocalDate.now().plusDays(2))
                        .status((short)1)
                        .build()
                ))
                .build();
        AltenChallengeException exception = Assertions.assertThrows(AltenChallengeException.class, () -> {
            BookingDTO response = reservationService.book(bookingDTO);
        });

        Assertions.assertEquals("There was an issue in the request",exception.getApiError().getMessage());
        Assertions.assertEquals("Invalid 'from' date provided: 'From' date has to be at least tomorrow",
                exception.getApiError().getDebugMessage());

    }

    @Test
    void bookRoomAlreadyBookedException() throws AltenChallengeException {
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
                        .stayFrom(LocalDate.now().plusDays(1))
                        .stayTo(LocalDate.now().plusDays(3))
                        .status((short)1)
                        .build()
                ))
                .build();
        Reservation reservation = Mapper.reservationDtoToEntity(bookingDTO.getReservation());
        reservation.setId(UUID.randomUUID());
        List<ReservationRoom> reservationRooms = Mapper.createReservationRoomsFromDto(bookingDTO.getRooms(), reservation);
        BDDMockito.when(reservationRoomRepository.findByRoomsAndDates(BDDMockito.any(),BDDMockito.any(), BDDMockito.any()))
                .thenReturn(List.of(reservationRooms.get(0)));

        AltenChallengeException exception = Assertions.assertThrows(AltenChallengeException.class, () -> {
            BookingDTO response = reservationService.book(bookingDTO);
        });

        Assertions.assertEquals("There was an issue trying to book the reservation",exception.getApiError().getMessage());
        Assertions.assertEquals("One of the rooms selected is already booked",exception.getApiError().getDebugMessage());

    }

    @Test
    void bookFromDateIsPriorTodayErrorException() throws AltenChallengeException {
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
                        .stayFrom(LocalDate.now().minusDays(1))
                        .stayTo(LocalDate.now().plusDays(2))
                        .status((short)1)
                        .build()
                ))
                .build();
        AltenChallengeException exception = Assertions.assertThrows(AltenChallengeException.class, () -> {
            BookingDTO response = reservationService.book(bookingDTO);
        });

        Assertions.assertEquals("There was an issue in the request",exception.getApiError().getMessage());
        Assertions.assertEquals("Invalid 'from' date provided: 'From' date cannot be before than today",exception.getApiError().getDebugMessage());
    }

    @Test
    void bookFromMore30DaysInAdvanceException() throws AltenChallengeException {
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
                        .stayFrom(LocalDate.now().plusDays(31))
                        .stayTo(LocalDate.now().plusDays(33))
                        .status((short)1)
                        .build()
                ))
                .build();
        AltenChallengeException exception = Assertions.assertThrows(AltenChallengeException.class, () -> {
            BookingDTO response = reservationService.book(bookingDTO);
        });


        Assertions.assertEquals("There was an issue in the request",exception.getApiError().getMessage());
        Assertions.assertEquals("Invalid 'From' date provided for booking: You cannot book for more than 30 days in advance",exception.getApiError().getDebugMessage());
    }

    @Test
    void bookToPriorFromException() throws AltenChallengeException {
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
                        .stayFrom(LocalDate.now().plusDays(2))
                        .stayTo(LocalDate.now().plusDays(1))
                        .status((short)1)
                        .build()
                ))
                .build();
        AltenChallengeException exception = Assertions.assertThrows(AltenChallengeException.class, () -> {
            BookingDTO response = reservationService.book(bookingDTO);
        });

        Assertions.assertEquals("There was an issue in the request",exception.getApiError().getMessage());
        Assertions.assertEquals("Invalid 'to' date provided: 'To' date cannot be before than 'From'",exception.getApiError().getDebugMessage());
    }

    @Test
    void bookToPriorTodayException() throws AltenChallengeException {
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
                        .stayFrom(LocalDate.now().plusDays(1))
                        .stayTo(LocalDate.now().minusDays(1))
                        .status((short)1)
                        .build()
                ))
                .build();
        AltenChallengeException exception = Assertions.assertThrows(AltenChallengeException.class, () -> {
            BookingDTO response = reservationService.book(bookingDTO);
        });

        Assertions.assertEquals("There was an issue in the request",exception.getApiError().getMessage());
        Assertions.assertEquals("Invalid 'to' date provided: 'To' date cannot be before than today",exception.getApiError().getDebugMessage());
    }

    @Test
    void bookStayPeriodGreater3DaysException() throws AltenChallengeException {
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
                        .stayFrom(LocalDate.now().plusDays(1))
                        .stayTo(LocalDate.now().plusDays(5))
                        .status((short)1)
                        .build()
                ))
                .build();
        AltenChallengeException exception = Assertions.assertThrows(AltenChallengeException.class, () -> {
            BookingDTO response = reservationService.book(bookingDTO);
        });
        Assertions.assertEquals("There was an issue in the request",exception.getApiError().getMessage());
        Assertions.assertEquals("Invalid 'to' date provided for booking: Stay period for reservation cannot be more than 3 days",exception.getApiError().getDebugMessage());
    }

    @Test
    void modifyReservationSuccess() throws AltenChallengeException {
        BookingDTO bookingDTO = BookingDTO.builder()
                .reservation(ReservationDTO.builder()
                        .id(UUID.randomUUID())
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
                        .stayFrom(LocalDate.now().plusDays(1))
                        .stayTo(LocalDate.now().plusDays(3))
                        .status((short)1)
                        .build()
                ))
                .build();
        Reservation reservation = Mapper.reservationDtoToEntity(bookingDTO.getReservation());
        List<ReservationRoom> reservationRooms = Mapper.createReservationRoomsFromDto(bookingDTO.getRooms(),reservation);
        BDDMockito.when(repository.findByIdAndStatus(bookingDTO.getReservation().getId(), (short) 1)).thenReturn(Optional.of(reservation));
        BDDMockito.when(repository.saveAndFlush(BDDMockito.any())).thenReturn(reservation);
        BDDMockito.when(reservationRoomRepository.findByIdAndRoomsAndDates(BDDMockito.any(),BDDMockito.any(),BDDMockito.any(),BDDMockito.any())).thenReturn(Collections.emptyList());
        BDDMockito.when(reservationRoomRepository.findByReservationIdAndRoomId(reservation.getId(),12L)).thenReturn(Optional.of(reservationRooms.get(0)));
        BDDMockito.when(reservationRoomRepository.saveAll(BDDMockito.any())).thenReturn(reservationRooms);

        BookingDTO response = reservationService.modifyReservation(bookingDTO);
        Assertions.assertEquals(bookingDTO.getReservation().getId(), response.getReservation().getId());
        Assertions.assertEquals(bookingDTO.getRooms().size(), response.getRooms().size());

    }

    @Test
    void modifyReservationNoReservationIdException() throws AltenChallengeException {
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
                        .stayFrom(LocalDate.now().plusDays(1))
                        .stayTo(LocalDate.now().plusDays(3))
                        .status((short)1)
                        .build()
                ))
                .build();

        AltenChallengeException exception = Assertions.assertThrows(AltenChallengeException.class, () -> {
            BookingDTO response = reservationService.modifyReservation(bookingDTO);
        });

        Assertions.assertEquals("There was an issue trying to modify the reservation", exception.getApiError().getMessage());
        Assertions.assertEquals("You're trying to modify an reservation but not reservationId was provided", exception.getApiError().getDebugMessage());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getApiError().getStatus());
    }

    @Test
    void modifyReservationNoReservationNoExistsException() throws AltenChallengeException {
        BookingDTO bookingDTO = BookingDTO.builder()
                .reservation(ReservationDTO.builder()
                        .id(UUID.randomUUID())
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
                        .stayFrom(LocalDate.now().plusDays(1))
                        .stayTo(LocalDate.now().plusDays(3))
                        .status((short)1)
                        .build()
                ))
                .build();

        BDDMockito.when(repository.findByIdAndStatus(bookingDTO.getReservation().getId(),(short) 1)).thenReturn(Optional.empty());

        AltenChallengeException exception = Assertions.assertThrows(AltenChallengeException.class, () -> {
            BookingDTO response = reservationService.modifyReservation(bookingDTO);
        });

        Assertions.assertEquals("There was an issue trying to modify the reservation", exception.getApiError().getMessage());
        Assertions.assertEquals("Provided reservation id was not found in the system", exception.getApiError().getDebugMessage());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getApiError().getStatus());
    }

   @Test
    void modifyReservationBookedRoomException() throws AltenChallengeException {
       BookingDTO bookingDTO = BookingDTO.builder()
               .reservation(ReservationDTO.builder()
                       .id(UUID.randomUUID())
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
                       .stayFrom(LocalDate.now().plusDays(1))
                       .stayTo(LocalDate.now().plusDays(3))
                       .status((short)1)
                       .build()
               ))
               .build();
       Reservation reservation = Mapper.reservationDtoToEntity(bookingDTO.getReservation());
       BDDMockito.when(repository.findByIdAndStatus(bookingDTO.getReservation().getId(),(short) 1)).thenReturn(Optional.of(reservation));
       BDDMockito.when(repository.saveAndFlush(BDDMockito.any())).thenReturn(reservation);
       BDDMockito.when(reservationRoomRepository.findByIdAndRoomsAndDates(BDDMockito.any(),BDDMockito.any(),BDDMockito.any(),BDDMockito.any())).thenReturn(List.of(ReservationRoom.builder().build()));


       AltenChallengeException exception = Assertions.assertThrows(AltenChallengeException.class, () -> {
            BookingDTO response = reservationService.modifyReservation(bookingDTO);
        });

        Assertions.assertEquals("There was an issue trying to modify the reservation", exception.getApiError().getMessage());
        Assertions.assertEquals("One of the rooms selected is already booked", exception.getApiError().getDebugMessage());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getApiError().getStatus());
    }

    @Test
    void cancelReservationSuccess() throws AltenChallengeException {
        UUID reservationId = UUID.randomUUID();
        Reservation reservation = Reservation.builder()
                .id(reservationId)
                .visitorPhone("0983921382")
                .visitorEmail("valeska.salazar6@gmail.com")
                .visitorName("Valeska Salazar")
                .reservationRooms(List.of(
                        ReservationRoom.builder()
                                .id(6L)
                                .room(Room.builder()
                                    .id(12L)
                                    .roomView("Sea View")
                                    .roomType(RoomType.builder()
                                        .id(2L)
                                        .name("Senior")
                                        .beds(List.of(RoomTypeBed.builder()
                                                                .bedType(Bed.QUEEN)
                                                                .quantity((short)1)
                                                                .build(),
                                                        RoomTypeBed.builder()
                                                                .bedType(Bed.SINGLE)
                                                                .quantity((short)1)
                                                                .build()
                                                )
                                        )
                                        .build()
                                    )
                                    .build()
                                )
                                .stayFrom(LocalDate.now().plusDays(1).atTime(0,0,0))
                                .stayTo(LocalDate.now().plusDays(3).atTime(23,59,59))
                                .status((short)1)
                                .build()
                        )
                )
                .status((short) 1)
                .build();
        Reservation reservationCancelled = Reservation.builder()
                .id(reservationId)
                .visitorPhone("0983921382")
                .visitorEmail("valeska.salazar6@gmail.com")
                .visitorName("Valeska Salazar")
                .reservationRooms(List.of(
                                ReservationRoom.builder()
                                        .id(6L)
                                        .room(Room.builder()
                                                .id(12L)
                                                .roomView("Sea View")
                                                .roomType(RoomType.builder()
                                                        .id(2L)
                                                        .name("Senior")
                                                        .beds(List.of(RoomTypeBed.builder()
                                                                                .bedType(Bed.QUEEN)
                                                                                .quantity((short)1)
                                                                                .build(),
                                                                        RoomTypeBed.builder()
                                                                                .bedType(Bed.SINGLE)
                                                                                .quantity((short)1)
                                                                                .build()
                                                                )
                                                        )
                                                        .build()
                                                )
                                                .build()
                                        )
                                        .stayFrom(LocalDate.now().plusDays(1).atTime(0,0,0))
                                        .stayTo(LocalDate.now().plusDays(3).atTime(23,59,59))
                                        .status((short)0)
                                        .build()
                        )
                )
                .status((short) 0)
                .build();
        BDDMockito.when(repository.findById(reservation.getId())).thenReturn(Optional.of(reservation));
        BDDMockito.when(repository.saveAndFlush(BDDMockito.any())).thenReturn(reservationCancelled);
        reservationService.cancelReservation(reservationId);
    }

    @Test
    void cancelReservationNoIdException() throws AltenChallengeException {
        AltenChallengeException exception = Assertions.assertThrows(AltenChallengeException.class, () ->{
            reservationService.cancelReservation(null);
        });
        Assertions.assertEquals("There was an issue trying to cancel the reservation", exception.getApiError().getMessage());
        Assertions.assertEquals("No reservation ID was provided", exception.getApiError().getDebugMessage());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getApiError().getStatus());
    }

    @Test
    void cancelReservationNoExistsException() throws AltenChallengeException {
        UUID reservationId = UUID.randomUUID();

        BDDMockito.when(repository.findById(reservationId)).thenReturn(Optional.empty());
        AltenChallengeException exception = Assertions.assertThrows(AltenChallengeException.class, () ->{
            reservationService.cancelReservation(reservationId);
        });
        Assertions.assertEquals("There was an issue trying to cancel the reservation", exception.getApiError().getMessage());
        Assertions.assertEquals("Provided reservation id was not found in the system", exception.getApiError().getDebugMessage());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getApiError().getStatus());
    }

    @Test
    void getReservationSuccess() throws AltenChallengeException {
        UUID reservationId = UUID.randomUUID();
        Reservation reservation = Reservation.builder()
                .id(reservationId)
                .visitorPhone("0983921382")
                .visitorEmail("valeska.salazar6@gmail.com")
                .visitorName("Valeska Salazar")
                .reservationRooms(List.of(
                                ReservationRoom.builder()
                                        .id(6L)
                                        .room(Room.builder()
                                                .id(12L)
                                                .roomView("Sea View")
                                                .roomType(RoomType.builder()
                                                        .id(2L)
                                                        .name("Senior")
                                                        .beds(List.of(RoomTypeBed.builder()
                                                                                .bedType(Bed.QUEEN)
                                                                                .quantity((short)1)
                                                                                .build(),
                                                                        RoomTypeBed.builder()
                                                                                .bedType(Bed.SINGLE)
                                                                                .quantity((short)1)
                                                                                .build()
                                                                )
                                                        )
                                                        .build()
                                                )
                                                .build()
                                        )
                                        .stayFrom(LocalDate.now().plusDays(1).atTime(0,0,0))
                                        .stayTo(LocalDate.now().plusDays(3).atTime(23,59,59))
                                        .status((short)1)
                                        .build()
                        )
                )
                .status((short) 1)
                .build();
        BDDMockito.when(repository.findByIdAndStatus(reservationId, (short) 1)).thenReturn(Optional.of(reservation));

        BookingDTO response = reservationService.getReservation(reservationId);
        Assertions.assertEquals(reservation.getId(),response.getReservation().getId());
        Assertions.assertEquals(reservation.getReservationRooms().size(),response.getRooms().size());
    }

    @Test
    void getReservationByIdNullException() throws AltenChallengeException {

        AltenChallengeException exception = Assertions.assertThrows(AltenChallengeException.class, () ->{
            BookingDTO response = reservationService.getReservation(null);
        });
        Assertions.assertEquals("There was an issue trying to get the reservation", exception.getApiError().getMessage());
        Assertions.assertEquals("No reservation ID was provided", exception.getApiError().getDebugMessage());
    }

    @Test
    void getReservationNotExistsException() throws AltenChallengeException {
        UUID reservationId = UUID.randomUUID();

        BDDMockito.when(repository.findByIdAndStatus(reservationId, (short) 1)).thenReturn(Optional.empty());

        AltenChallengeException exception = Assertions.assertThrows(AltenChallengeException.class, () ->{
            BookingDTO response = reservationService.getReservation(reservationId);
        });
        Assertions.assertEquals("There was an issue trying to get the reservation", exception.getApiError().getMessage());
        Assertions.assertEquals("Provided reservation id was not found in the system", exception.getApiError().getDebugMessage());
    }
}