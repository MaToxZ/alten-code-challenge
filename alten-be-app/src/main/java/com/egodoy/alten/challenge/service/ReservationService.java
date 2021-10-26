package com.egodoy.alten.challenge.service;

import com.egodoy.alten.challenge.exception.AltenChallengeException;
import com.egodoy.alten.challenge.model.dto.BookingDTO;

import java.util.UUID;

public interface ReservationService {
    BookingDTO book(BookingDTO reservation) throws AltenChallengeException;
    BookingDTO modifyReservation(BookingDTO reservation) throws AltenChallengeException;
    void cancelReservation(UUID reservationId) throws AltenChallengeException;
    BookingDTO getReservation(UUID reservationId) throws AltenChallengeException;
}
