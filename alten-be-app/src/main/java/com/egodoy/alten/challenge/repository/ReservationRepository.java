package com.egodoy.alten.challenge.repository;

import com.egodoy.alten.challenge.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, UUID> {

    /**
     * This query retrieve a reservation based in a given reservation id and status. The idea of this query is to validate
     * if a reservation exists before modifying, cancelling or consulting it.
     * @param id Reservation Id to be searched
     * @param status 0 for cancelled reservation, 1 for active reservation
     * @return Reservation record if match the criteria
     */
    Optional<Reservation> findByIdAndStatus(UUID id, Short status);

}
