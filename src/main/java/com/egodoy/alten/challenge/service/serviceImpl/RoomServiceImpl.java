package com.egodoy.alten.challenge.service.serviceImpl;

import com.egodoy.alten.challenge.exception.AltenChallengeException;
import com.egodoy.alten.challenge.model.Room;
import com.egodoy.alten.challenge.model.dto.RoomAvailabilityDto;
import com.egodoy.alten.challenge.repository.RoomRepository;
import com.egodoy.alten.challenge.service.RoomService;
import com.egodoy.alten.challenge.utils.Mapper;
import com.egodoy.alten.challenge.utils.PaginatedResult;
import com.egodoy.alten.challenge.utils.validator.ReservationDTOValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;

    @Autowired
    public RoomServiceImpl(RoomRepository roomRepository){
        this.roomRepository = roomRepository;
    }

    /**
     * This method will find available rooms based on a date range. The method extract the dates interval contained between
     * both provided dates 'From' and 'To', then queries the database to search find whether rooms that are active and
     * don't have reservations or rooms that have reservation in the provided period. Once it gets the rooms from the
     * database, iterates over them to look at their reservations (for those that have) and once again extract the dates
     * between the stayFrom and stayTo of each reservation that the room has and stored them in a temp array, subsequently
     * it compare the main period dates (the search period) against the reserved dates for that room, to filter those dates
     * that are available for the room, and then return that list as availableDates for it. This process is performed for
     * each room returned by the database, once it finished, It wraps into and PaginatedResult object the list of rooms
     * containing the date availability for each and returned this wrap object to the user.
     * @param from object containing date 'From'. This needs to be at least a day ahead of the current search day
     * @param to object containing date 'To'
     * @param pageIndex page number for pagination (defaulted to 0 if it's not provided)
     * @param pageSize page size for pagination (defaulted to 25 if it's not provided)
     * @return Paginated Result object will be returned
     * @throws AltenChallengeException if validations fail
     */
    @Override
    public PaginatedResult<RoomAvailabilityDto> findAvailabilityByDateRange(LocalDate from, LocalDate to,
                                                                Optional<Integer> pageIndex, Optional<Integer> pageSize) throws AltenChallengeException {
            if(ReservationDTOValidator.validateFromToDates(from,to)) {
                Pageable pageable = PageRequest.of(pageIndex.orElse(0), pageSize.orElse(25));
                Page<Room> rooms = roomRepository.findAvailabilityByFromTo(from.atTime(0,0,0), to.atTime(23,59,59), pageable);
                List<LocalDate> MainPeriod = from.datesUntil(to.plusDays(1)).collect(Collectors.toList());
                List<LocalDate> reservedPeriod = new LinkedList<>();
                List<RoomAvailabilityDto> roomAvailabilityDtos = rooms.toList().stream()
                        .map(room -> {
                            reservedPeriod.clear();
                            room.getReservationRooms().stream()
                                    .forEach(reservationRoom -> {
                                        reservedPeriod.addAll(reservationRoom.getStayFrom()
                                                .toLocalDate()
                                                .datesUntil(
                                                        reservationRoom.getStayTo().plusDays(1).toLocalDate()
                                                ).collect(Collectors.toList()));
                                    });
                            return RoomAvailabilityDto.builder()
                                    .room(Mapper.roomDtoToRoom(room))
                                    .availableDates(this.mergePeriodForAvailableDate(MainPeriod,reservedPeriod))
                                    .build();
                        })
                        .collect(Collectors.toList());
                return PaginatedResult.<RoomAvailabilityDto>builder()
                        .data(roomAvailabilityDtos)
                        .pageNumber(pageable.getPageNumber())
                        .pageSize(pageable.getPageSize())
                        .totalRecords(rooms.getTotalElements())
                        .build();
            }
        return null;
    }

    private List<LocalDate> mergePeriodForAvailableDate(List<LocalDate> mainPeriod, List<LocalDate> reservedPeriod){
        if(!reservedPeriod.isEmpty()){
            return mainPeriod.stream()
                    .filter(localDate -> !reservedPeriod.contains(localDate))
                    .collect(Collectors.toList());
        }
        return mainPeriod.stream().collect(Collectors.toList());
    }
}
