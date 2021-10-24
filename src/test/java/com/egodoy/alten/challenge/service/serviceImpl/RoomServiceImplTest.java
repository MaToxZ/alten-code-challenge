package com.egodoy.alten.challenge.service.serviceImpl;

import com.egodoy.alten.challenge.exception.AltenChallengeException;
import com.egodoy.alten.challenge.model.ReservationRoom;
import com.egodoy.alten.challenge.model.Room;
import com.egodoy.alten.challenge.model.RoomType;
import com.egodoy.alten.challenge.model.RoomTypeBed;
import com.egodoy.alten.challenge.model.dto.ReservationDTO;
import com.egodoy.alten.challenge.model.dto.RoomAvailabilityDto;
import com.egodoy.alten.challenge.model.dto.RoomDTO;
import com.egodoy.alten.challenge.model.enums.Bed;
import com.egodoy.alten.challenge.repository.RoomRepository;
import com.egodoy.alten.challenge.utils.PaginatedResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ExtendWith(SpringExtension.class)
class RoomServiceImplTest {

   @InjectMocks
    private RoomServiceImpl roomService;

    @Mock
    private RoomRepository roomRepository;

    @Test
    void findAvailabilityByDateRangeFullSuccess() throws AltenChallengeException {

        LocalDate from = LocalDate.now().plusDays(1);
        LocalDate to = LocalDate.now().plusDays(11);
        Room room = Room.builder()
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
                .reservationRooms(new ArrayList<>())
                .build();
        Pageable pageable = PageRequest.of(0, 25);
        PageImpl page = new PageImpl(List.of(room),pageable,1);

        List<LocalDate> availability = from.datesUntil(to.plusDays(1))
                        .collect(Collectors.toList());

        BDDMockito.when(roomRepository.findAvailabilityByFromTo(BDDMockito.any(), BDDMockito.any() , BDDMockito.any()))
                .thenReturn(page);
        PaginatedResult<RoomAvailabilityDto> response = roomService.findAvailabilityByDateRange(from, to, Optional.empty(),Optional.empty());
        Assertions.assertEquals(1, response.getData().size());
        Assertions.assertEquals(availability, response.getData().get(0).getAvailableDates());
    }

    @Test
    void findAvailabilityByDateRangeReducedSuccess() throws AltenChallengeException {

        LocalDate from = LocalDate.now().plusDays(1);
        LocalDate to = LocalDate.now().plusDays(11);
        Room room = Room.builder()
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
                .reservationRooms(List.of(
                            ReservationRoom.builder()
                                .id(2L)
                                .status((short) 1)
                                .stayFrom(LocalDateTime.of(2021,10,23,0,0,0))
                                .stayTo(LocalDateTime.of(2021,10,25,23,59,59))
                                .build(),
                            ReservationRoom.builder()
                                .id(2L)
                                .status((short) 1)
                                .stayFrom(LocalDateTime.of(2021,10,29,0,0,0))
                                .stayTo(LocalDateTime.of(2021,10,31,23,59,59))
                                .build()
                        )
                )
                .build();
        Pageable pageable = PageRequest.of(0, 25);
        PageImpl page = new PageImpl(List.of(room),pageable,1);

        List<LocalDate> availability = from.datesUntil(to.plusDays(1))
                .collect(Collectors.toList());
        availability.removeAll(room.getReservationRooms().get(0).getStayFrom().toLocalDate()
                .datesUntil(room.getReservationRooms().get(0).getStayTo().toLocalDate().plusDays(1))
                .collect(Collectors.toList())
        );

        availability.removeAll(room.getReservationRooms().get(1).getStayFrom().toLocalDate()
                .datesUntil(room.getReservationRooms().get(1).getStayTo().toLocalDate().plusDays(1))
                .collect(Collectors.toList())
        );

        BDDMockito.when(roomRepository.findAvailabilityByFromTo(BDDMockito.any(), BDDMockito.any() , BDDMockito.any()))
                .thenReturn(page);
        PaginatedResult<RoomAvailabilityDto> response = roomService.findAvailabilityByDateRange(from, to, Optional.empty(),Optional.empty());
        Assertions.assertEquals(1, response.getData().size());
        Assertions.assertEquals(availability, response.getData().get(0).getAvailableDates());
    }

    @Test
    void findAvailabilityByDateRangeFromIsNull() throws AltenChallengeException {

        AltenChallengeException exception = Assertions.assertThrows(AltenChallengeException.class, () -> {
            PaginatedResult<RoomAvailabilityDto> response =
                    roomService.findAvailabilityByDateRange(null,LocalDate.now(), Optional.empty(),Optional.empty());
        });
        Assertions.assertEquals("Invalid date range provided: You need to provide both 'From' and 'To' for this action",exception.getApiError().getDebugMessage());
        Assertions.assertEquals("There was an issue in the request",exception.getApiError().getMessage());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST,exception.getApiError().getStatus());

    }

    @Test
    void findAvailabilityByDateRangeToIsNull() throws AltenChallengeException {

        AltenChallengeException exception = Assertions.assertThrows(AltenChallengeException.class, () -> {
            PaginatedResult<RoomAvailabilityDto> response =
                    roomService.findAvailabilityByDateRange(LocalDate.now(), null, Optional.empty(),Optional.empty());
        });
        Assertions.assertEquals("Invalid date range provided: You need to provide both 'From' and 'To' for this action",exception.getApiError().getDebugMessage());
        Assertions.assertEquals("There was an issue in the request",exception.getApiError().getMessage());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST,exception.getApiError().getStatus());

    }

    @Test
    void findAvailabilityByDateRangeEmpty() throws AltenChallengeException {
        LocalDate from = LocalDate.now().plusDays(1);
        LocalDate to = LocalDate.now().plusDays(4);
        Pageable pageable = PageRequest.of(0, 25);
        PageImpl page = new PageImpl(Collections.emptyList(),pageable,0);

        BDDMockito.when(roomRepository.findAvailabilityByFromTo(BDDMockito.eq(from.atTime(0,0,0)),
                        BDDMockito.eq(to.atTime(23,59,59)), BDDMockito.any()))
                .thenReturn(page);
        PaginatedResult<RoomAvailabilityDto> response = roomService.findAvailabilityByDateRange(from,to, Optional.empty(),Optional.empty());
        Assertions.assertEquals(0, response.getData().size());
    }


    @Test
    void findAvailabilityByDateMissingFromPriorException() throws AltenChallengeException {
        LocalDate from = LocalDate.now().minusDays(1);
        LocalDate to = LocalDate.now().plusDays(10);
        AltenChallengeException exception = Assertions.assertThrows(AltenChallengeException.class, () -> {
            PaginatedResult<RoomAvailabilityDto> response = roomService.findAvailabilityByDateRange(from, to, Optional.empty(),Optional.empty());
        });
        Assertions.assertEquals("Invalid 'from' date provided: 'From' date cannot be before than today",exception.getApiError().getDebugMessage());
        Assertions.assertEquals("There was an issue in the request",exception.getApiError().getMessage());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST,exception.getApiError().getStatus());
    }

    @Test
    void findAvailabilityByDateMissingToPriorException() throws AltenChallengeException {
        LocalDate from = LocalDate.now().plusDays(1);
        LocalDate to = LocalDate.now().minusDays(11);
        AltenChallengeException exception = Assertions.assertThrows(AltenChallengeException.class, () -> {
            PaginatedResult<RoomAvailabilityDto> response = roomService.findAvailabilityByDateRange(from, to, Optional.empty(),Optional.empty());
        });
        Assertions.assertEquals("Invalid 'to' date provided: 'To' date cannot be before than today",exception.getApiError().getDebugMessage());
        Assertions.assertEquals("There was an issue in the request",exception.getApiError().getMessage());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST,exception.getApiError().getStatus());
    }

    @Test
    void findAvailabilityByDateMissingToPriorFromException() throws AltenChallengeException {
        LocalDate from = LocalDate.now().plusDays(10);
        LocalDate to = LocalDate.now().plusDays(8);
        AltenChallengeException exception = Assertions.assertThrows(AltenChallengeException.class, () -> {
            PaginatedResult<RoomAvailabilityDto> response = roomService.findAvailabilityByDateRange(from, to, Optional.empty(),Optional.empty());
        });
        Assertions.assertEquals("Invalid 'to' date provided: 'To' date cannot be before than 'From'",exception.getApiError().getDebugMessage());
        Assertions.assertEquals("There was an issue in the request",exception.getApiError().getMessage());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST,exception.getApiError().getStatus());
    }
}