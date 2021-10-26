package com.egodoy.alten.challenge.utils.validator;

import com.egodoy.alten.challenge.exception.AltenChallengeApiError;
import com.egodoy.alten.challenge.exception.AltenChallengeException;
import com.egodoy.alten.challenge.model.dto.BookingDTO;
import com.egodoy.alten.challenge.model.dto.ReservationRoomDTO;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

/**
 * Validator Class that handle date validations to avoid bad data to be persisted
 */
public class ReservationDTOValidator {
    private static final String errorMessage = "There was an issue in the request";

    private ReservationDTOValidator(){

    }

    /**
     * This method validates if the provided reservation object contains the appropriate date data for booking reservation
     * @param reservationDTO object containing reservation related details
     * @return TRUE if everything goes well
     * @throws AltenChallengeException If validations fail
     */
    public static boolean validateReservationDto(BookingDTO reservationDTO) throws AltenChallengeException {
        if(Objects.nonNull(reservationDTO)){
            if(validateReservationObject(reservationDTO)) {
                List<ReservationRoomDTO> rooms = reservationDTO.getRooms();
                if (Objects.nonNull(rooms) && !rooms.isEmpty()) {
                    for (int i = 0; i < rooms.size(); i++) {
                        if (Objects.nonNull(rooms.get(i).getRoom().getId())) {
                            validateFromToDatesForBooking(rooms.get(i).getStayFrom(), rooms.get(i).getStayTo());
                        } else {
                            throw new AltenChallengeException(AltenChallengeApiError.builder()
                                    .message(errorMessage)
                                    .debugMessage("Some of the provided rooms are invalid")
                                    .status(HttpStatus.BAD_REQUEST)
                                    .build()
                            );
                        }
                    }
                }else{
                    throw  new AltenChallengeException(AltenChallengeApiError.builder()
                            .message(errorMessage)
                            .debugMessage("You need to provide at least a room to book")
                            .status(HttpStatus.BAD_REQUEST)
                            .build()
                    );
                }
            }
        }else{
            throw  new AltenChallengeException(AltenChallengeApiError.builder()
                    .message(errorMessage)
                    .debugMessage("No reservation object provided")
                    .status(HttpStatus.BAD_REQUEST)
                    .build()
            );
        }
        return true;
    }

    /**
     * This methods validates date validity and constraints
     * @param from when the reservation starts
     * @param to when the reservation ends
     * @return TRUE if everything goes well
     * @throws AltenChallengeException if validations fail
     */
    public static boolean validateFromToDatesForBooking(LocalDate from, LocalDate to) throws AltenChallengeException {
        if(validateFromToDates(from,to)){
            LocalDate now = LocalDate.now();
            if(ChronoUnit.DAYS.between(from,to) > 3){
                throw  new AltenChallengeException(AltenChallengeApiError.builder()
                        .message(errorMessage)
                        .debugMessage("Invalid 'to' date provided for booking: Stay period for reservation cannot be more than 3 days")
                        .status(HttpStatus.BAD_REQUEST)
                        .build()
                );
            }
            if(ChronoUnit.DAYS.between(now,from) > 30){
                throw  new AltenChallengeException(AltenChallengeApiError.builder()
                        .message(errorMessage)
                        .debugMessage("Invalid 'From' date provided for booking: You cannot book for more than 30 days in advance")
                        .status(HttpStatus.BAD_REQUEST)
                        .build()
                );
            }
        }
        return true;
    }

    /**
     * Generic Date validations around the allowed queries that the system supports
     * @param from Beginning of stay date, it might be for booking or searching availability
     * @param to Ending of the stay date, it might be for booking or searching availability
     * @return TRUE if everything goes well
     * @throws AltenChallengeException if Validation fail
     */
    public static boolean validateFromToDates(LocalDate from, LocalDate to) throws AltenChallengeException {
        if(Objects.nonNull(from) && Objects.nonNull(to)){
            LocalDate now = LocalDate.now();
            if(from.isBefore(now))
                throw  new AltenChallengeException(AltenChallengeApiError.builder()
                        .message(errorMessage)
                        .debugMessage("Invalid 'from' date provided: 'From' date cannot be before than today")
                        .status(HttpStatus.BAD_REQUEST)
                        .build()
                );
            if(from.isEqual(now))
                throw  new AltenChallengeException(AltenChallengeApiError.builder()
                        .message(errorMessage)
                        .debugMessage("Invalid 'from' date provided: 'From' date has to be at least tomorrow")
                        .status(HttpStatus.BAD_REQUEST)
                        .build()
                );
            if(to.isBefore(now))
                throw  new AltenChallengeException(AltenChallengeApiError.builder()
                        .message(errorMessage)
                        .debugMessage("Invalid 'to' date provided: 'To' date cannot be before than today")
                        .status(HttpStatus.BAD_REQUEST)
                        .build()
                );
            if(to.isBefore(from)){
                throw  new AltenChallengeException(AltenChallengeApiError.builder()
                        .message(errorMessage)
                        .debugMessage("Invalid 'to' date provided: 'To' date cannot be before than 'From'")
                        .status(HttpStatus.BAD_REQUEST)
                        .build()
                );
            }
        }else {
            throw  new AltenChallengeException(AltenChallengeApiError.builder()
                    .message(errorMessage)
                    .debugMessage("Invalid date range provided: You need to provide both 'From' and 'To' for this action")
                    .status(HttpStatus.BAD_REQUEST)
                    .build()
            );
        }
        return true;
    }

    /**
     * This methods validate that a BookingDto has the visitor information before proceeding to place the reservation
     * @param reservation BookingDTO object containing reservation details
     * @return True if everything goes well
     * @throws AltenChallengeException if validations fail
     */
    public static boolean validateReservationObject(BookingDTO reservation) throws AltenChallengeException {

        if(Objects.nonNull(reservation.getReservation())){
            if(Objects.isNull(reservation.getReservation().getVisitorEmail())
                    || reservation.getReservation().getVisitorEmail().isEmpty()){
                throw  new AltenChallengeException(AltenChallengeApiError.builder()
                        .message(errorMessage)
                        .debugMessage("Invalid Reservation: You need to provide the 'visitorEmail'")
                        .status(HttpStatus.BAD_REQUEST)
                        .build()
                );
            }
            if(Objects.isNull(reservation.getReservation().getVisitorName())
                    || reservation.getReservation().getVisitorName().isEmpty()){
                throw  new AltenChallengeException(AltenChallengeApiError.builder()
                        .message(errorMessage)
                        .debugMessage("Invalid Reservation: You need to provide the 'visitorName'")
                        .status(HttpStatus.BAD_REQUEST)
                        .build()
                );
            }
            if(Objects.isNull(reservation.getReservation().getVisitorPhone())
                    || reservation.getReservation().getVisitorPhone().isEmpty()){
                throw  new AltenChallengeException(AltenChallengeApiError.builder()
                        .message(errorMessage)
                        .debugMessage("Invalid Reservation: You need to provide the 'visitorPhone'")
                        .status(HttpStatus.BAD_REQUEST)
                        .build()
                );
            }
            return true;
        }

        throw  new AltenChallengeException(AltenChallengeApiError.builder()
                .message(errorMessage)
                .debugMessage("Invalid Reservation: You need to provide Reservation object containing Visitor Information")
                .status(HttpStatus.BAD_REQUEST)
                .build()
        );
    }
}
