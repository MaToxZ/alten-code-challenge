package com.egodoy.alten.challenge.service;

import com.egodoy.alten.challenge.exception.AltenChallengeException;
import com.egodoy.alten.challenge.model.dto.RoomAvailabilityDto;
import com.egodoy.alten.challenge.utils.PaginatedResult;

import java.time.LocalDate;
import java.util.Optional;

public interface RoomService {

    PaginatedResult<RoomAvailabilityDto> findAvailabilityByDateRange(LocalDate from, LocalDate to,
                                                                     Optional<Integer> pageIndex, Optional<Integer> pageSize) throws AltenChallengeException;
}
