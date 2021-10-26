package com.egodoy.alten.challenge.controller;

import com.egodoy.alten.challenge.exception.AltenChallengeApiError;
import com.egodoy.alten.challenge.exception.AltenChallengeException;
import com.egodoy.alten.challenge.model.Room;
import com.egodoy.alten.challenge.model.RoomType;
import com.egodoy.alten.challenge.model.RoomTypeBed;
import com.egodoy.alten.challenge.model.dto.*;
import com.egodoy.alten.challenge.model.enums.Bed;
import com.egodoy.alten.challenge.service.serviceImpl.RoomServiceImpl;
import com.egodoy.alten.challenge.utils.PaginatedResult;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ExtendWith(SpringExtension.class)
class RoomControllerTest {

    @InjectMocks
    private RoomController roomController;

    @Mock
    private RoomServiceImpl roomService;

    @Test
    void getRoomAvailability() throws AltenChallengeException {
        LocalDate from = LocalDate.now();
        LocalDate to = LocalDate.now().plusDays(10);
        List<LocalDate> availability = from.datesUntil(to.plusDays(1))
                .collect(Collectors.toList());
        RoomAvailabilityDto roomAvailabilityDto = RoomAvailabilityDto.builder()
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
                        .build())
                .availableDates(availability)
                .build();
        PaginatedResult<RoomAvailabilityDto> paginatedResult = PaginatedResult.<RoomAvailabilityDto>builder()
                .data(List.of(roomAvailabilityDto))
                .pageNumber(0)
                .pageSize(25)
                .totalRecords(1L)
                .build();

        BDDMockito.when(roomService.findAvailabilityByDateRange(from,to, Optional.empty(),Optional.empty())).thenReturn(paginatedResult);

        ResponseEntity<PaginatedResult<RoomAvailabilityDto>> response = roomController.getRoomAvailability(from,to, Optional.empty(),Optional.empty());
        Assertions.assertEquals(paginatedResult.getTotalRecords(), response.getBody().getTotalRecords());
        Assertions.assertEquals(paginatedResult.getPageSize(), response.getBody().getPageSize());
        Assertions.assertEquals(paginatedResult.getPageNumber(), response.getBody().getPageNumber());
        Assertions.assertEquals(paginatedResult.getData().size(), response.getBody().getData().size());
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getRoomAvailabilityException() throws AltenChallengeException {
        LocalDate from = LocalDate.now();
        LocalDate to = LocalDate.now().plusDays(10);

        BDDMockito.willThrow(AltenChallengeException.builder().apiError(AltenChallengeApiError.builder().status(HttpStatus.BAD_REQUEST).build()).build())
                .given(roomService).findAvailabilityByDateRange(from,to, Optional.empty(),Optional.empty());

        AltenChallengeException exception = Assertions.assertThrows(AltenChallengeException.class, () -> {
            ResponseEntity<PaginatedResult<RoomAvailabilityDto>> response = roomController.getRoomAvailability(from,to, Optional.empty(),Optional.empty());
        });

        Assertions.assertEquals(HttpStatus.BAD_REQUEST, exception.getApiError().getStatus());
        Assertions.assertEquals("/room/availability", exception.getApiError().getEndpoint());
    }
}