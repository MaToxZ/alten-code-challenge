package com.egodoy.alten.challenge.service.serviceImpl;

import com.egodoy.alten.challenge.exception.AltenChallengeApiError;
import com.egodoy.alten.challenge.exception.AltenChallengeException;
import com.egodoy.alten.challenge.model.Reservation;
import com.egodoy.alten.challenge.model.ReservationRoom;
import com.egodoy.alten.challenge.model.dto.BookingDTO;
import com.egodoy.alten.challenge.model.dto.ReservationRoomDTO;
import com.egodoy.alten.challenge.repository.ReservationRepository;
import com.egodoy.alten.challenge.repository.ReservationRoomRepository;
import com.egodoy.alten.challenge.service.ReservationService;
import com.egodoy.alten.challenge.utils.Mapper;
import com.egodoy.alten.challenge.utils.validator.ReservationDTOValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;

    private final ReservationRoomRepository reservationRoomRepository;

    @Autowired
    public ReservationServiceImpl(ReservationRepository reservationRepository, ReservationRoomRepository reservationRoomRepository){
        this.reservationRepository = reservationRepository;
        this.reservationRoomRepository = reservationRoomRepository;
    }

    /**
     * This method is supposed to perform validations over the provided reservation object
     * if everything with provided dates, availability of the selected rooms among other validations are ok
     * a new Reservation will be booked. Otherwise, will return a Bad Request response describing the issue. If a
     * reservationId is provided this request is automatically redirected to ModifyReservation method.
     * @param reservation Reservation object containing all the details to be booked
     * @return saved reservation object
     * @throws AltenChallengeException If validations fail
     */
    @Override
    public BookingDTO book(BookingDTO reservation) throws AltenChallengeException {
        if(ReservationDTOValidator.validateReservationDto(reservation)){
            if(Objects.nonNull(reservation.getReservation().getId())){
                return this.modifyReservation(reservation);
            }else{
                List<ReservationRoomDTO> overlapRooms = reservation.getRooms().stream()
                        .filter(this::validateRoomOverlaps)
                        .collect(Collectors.toList());
                if(overlapRooms.isEmpty()){
                    Reservation savedReservation = reservationRepository.saveAndFlush(Mapper.reservationDtoToEntity(reservation.getReservation()));
                    List<ReservationRoom> reservationRooms = Mapper.createReservationRoomsFromDto(reservation.getRooms(), savedReservation);
                    return Mapper.reservationRoomsToBookingDto(reservationRoomRepository.saveAll(reservationRooms));
                }
            }
        }
        throw  new AltenChallengeException(AltenChallengeApiError.builder()
                .message("There was an issue trying to book the reservation")
                .debugMessage("One of the rooms selected is already booked")
                .status(HttpStatus.BAD_REQUEST)
                .build()
        );
    }

    /**
     * This method is supposed to modify a given reservation, after validations as existing of the provided reservation,
     * availability of room stay dates in case these were changed or added, provided dates, etc. If validations confirm
     * that modification is possible, the provided reservation will be modified. Otherwise, will return a Bad Request
     * response describing the issue. Modifications supported are reservation
     * status, room stay dates, room reservation status.
     * @param reservation Reservation Object containing modifications
     * @return modified reservation object
     * @throws AltenChallengeException If validations fail
     */
    @Override
    public BookingDTO modifyReservation(BookingDTO reservation) throws AltenChallengeException {
        if(ReservationDTOValidator.validateReservationDto(reservation)){
            if(Objects.nonNull(reservation.getReservation().getId())){
                Optional<Reservation> reservationEntity = reservationRepository.findByIdAndStatus(reservation.getReservation().getId(), (short) 1);
                if(reservationEntity.isPresent()){
                    reservationEntity.get().setStatus(reservation.getReservation().getStatus());
                    reservationEntity = Optional.of(reservationRepository.saveAndFlush(reservationEntity.get()));
                    List<ReservationRoomDTO> overlapRooms = reservation.getRooms().stream()
                            .filter(this::validateRoomModifyOverlaps)
                            .collect(Collectors.toList());
                    if(overlapRooms.isEmpty()) {
                        List<ReservationRoom> reservationRooms = Mapper.createReservationRoomsFromDto(reservation.getRooms(),reservationEntity.get());
                        return Mapper.reservationRoomsToBookingDto(reservationRoomRepository.saveAll(modifyReservationRooms(reservationRooms)));
                    }
                    throw  new AltenChallengeException(AltenChallengeApiError.builder()
                            .message("There was an issue trying to modify the reservation")
                            .debugMessage("One of the rooms selected is already booked")
                            .status(HttpStatus.BAD_REQUEST)
                            .build());
                }else{
                    throw  new AltenChallengeException(AltenChallengeApiError.builder()
                            .message("There was an issue trying to modify the reservation")
                            .debugMessage("Provided reservation id was not found in the system")
                            .status(HttpStatus.BAD_REQUEST)
                            .build());
                }
            }else{
                throw  new AltenChallengeException(AltenChallengeApiError.builder()
                        .message("There was an issue trying to modify the reservation")
                        .debugMessage("You're trying to modify an reservation but not reservationId was provided")
                        .status(HttpStatus.BAD_REQUEST)
                        .build());
            }
        }
        return null;
    }

    /**
     * This method is supposed to cancel a reservation based on a given reservationId, if the reservation is found
     * in the system, the reservation will be cancelled. Otherwise, will return a Bad Request response describing the issue.
     * @param reservationId Reservation Id to be cancelled
     * @throws AltenChallengeException If validations fail
     */
    @Override
    public void cancelReservation(UUID reservationId) throws AltenChallengeException {
        if(Objects.nonNull(reservationId)){
            Optional<Reservation> reservationEntity = reservationRepository.findById(reservationId);
            if(reservationEntity.isPresent()){
                reservationEntity.get().setStatus((short) 0);
                reservationEntity.get().getReservationRooms().stream()
                                .forEach(reservationRoom -> {
                                    reservationRoom.setStatus((short) 0);
                                });
                reservationRepository.saveAndFlush(reservationEntity.get());
            }else{
                throw  new AltenChallengeException(AltenChallengeApiError.builder()
                        .message("There was an issue trying to cancel the reservation")
                        .debugMessage("Provided reservation id was not found in the system")
                        .status(HttpStatus.BAD_REQUEST)
                        .build());
            }
        }else{
            throw  new AltenChallengeException(AltenChallengeApiError.builder()
                    .message("There was an issue trying to cancel the reservation")
                    .debugMessage("No reservation ID was provided")
                    .status(HttpStatus.BAD_REQUEST)
                    .build());
        }
    }

    /**
     * This methods retrieves a reservation based on the reservationId provided. The reservation needs to exist and be
     * active. Otherwise, will return a Bad Request response describing the issue.
     * @param reservationId Reservation Id to be retrieved
     * @return Object containing all the reservation details
     * @throws AltenChallengeException if validations fail
     */
    @Override
    public BookingDTO getReservation(UUID reservationId) throws AltenChallengeException {
        if(Objects.nonNull(reservationId)){
            Optional<Reservation> reservationEntity = reservationRepository.findByIdAndStatus(reservationId, (short) 1);
            if(reservationEntity.isPresent()){
                return Mapper.reservationToBookingDTO(reservationEntity.get());
            }else{
                throw  new AltenChallengeException(AltenChallengeApiError.builder()
                        .message("There was an issue trying to get the reservation")
                        .debugMessage("Provided reservation id was not found in the system")
                        .status(HttpStatus.BAD_REQUEST)
                        .build());
            }
        }else{
            throw  new AltenChallengeException(AltenChallengeApiError.builder()
                    .message("There was an issue trying to get the reservation")
                    .debugMessage("No reservation ID was provided")
                    .status(HttpStatus.BAD_REQUEST)
                    .build());
        }
    }

    private boolean validateRoomOverlaps(ReservationRoomDTO room){
        List<ReservationRoom> overLaps = reservationRoomRepository.findByRoomsAndDates(room.getRoom().getId(),
                room.getStayFrom().atTime(0,0,0),room.getStayTo().atTime(23,59,59));
        return overLaps.isEmpty() ? false : true;
    }

    private boolean validateRoomModifyOverlaps(ReservationRoomDTO room){
        List<ReservationRoom> overLaps = reservationRoomRepository.findByIdAndRoomsAndDates(room.getId(),room.getRoom().getId(),
                room.getStayFrom().atTime(0,0,0),room.getStayTo().atTime(23,59,59));
        return overLaps.isEmpty() ? false : true;
    }

    private List<ReservationRoom> modifyReservationRooms(List<ReservationRoom> reservationRooms) {
        return reservationRooms.stream()
                .map(reservationRoom -> {
                    Optional<ReservationRoom> savedReservation = reservationRoomRepository.findByReservationIdAndRoomId(reservationRoom.getReservation().getId(),
                            reservationRoom.getRoom().getId());
                    if (savedReservation.isPresent()) {
                        reservationRoom.setId(savedReservation.get().getId());
                        reservationRoom.setRoom(savedReservation.get().getRoom());
                    }
                    return reservationRoom;
                })
                .collect(Collectors.toList());


    }
}
