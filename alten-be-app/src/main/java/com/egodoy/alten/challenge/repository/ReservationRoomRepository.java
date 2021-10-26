package com.egodoy.alten.challenge.repository;

import com.egodoy.alten.challenge.model.ReservationRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ReservationRoomRepository extends JpaRepository<ReservationRoom, Long> {
    /**
     * This query return a list of reservation DTO based on the provided inputs. The main goal of this query is to find
     * other reservations of the room in mentioned between the date range provided, to prevent a new room
     * reservation to overlap and existing one.
     * @param roomId Room id requested to be booked
     * @param from Beginning stay date
     * @param to Ending stay date
     * @return List of existing reservation for that room between those dates if exist
     */
    @Query("Select rr From ReservationRoom rr JOIN rr.room r where r.id = :roomId " +
            "AND (:from BETWEEN rr.stayFrom AND rr.stayTo " +
            "OR :to BETWEEN rr.stayFrom AND rr.stayTo) " +
            "AND rr.status = 1")
    List<ReservationRoom> findByRoomsAndDates(@Param("roomId") Long roomId, @Param("from")LocalDateTime from, @Param("to") LocalDateTime to);

    /**
     * This query return a list of reservation DTO based on the provided inputs. The main goal of this query is to find
     * other reservations of the room in mentioned between the date range provided and excluding its own record based on
     * the reservationId, to prevent a modified or new room reservation to overlap and existing one.
     * @param rrId Reservation id in mentioned
     * @param roomId Room id requested to be booked
     * @param from Beginning stay date
     * @param to Ending stay date
     * @return List of existing reservation for that room between those dates if exist
     */
    @Query("Select rr From ReservationRoom rr JOIN rr.room r where r.id = :roomId " +
            "AND (:from BETWEEN rr.stayFrom AND rr.stayTo " +
            "OR :to BETWEEN rr.stayFrom AND rr.stayTo) " +
            "AND rr.status = 1 AND rr.id != :rrId")
    List<ReservationRoom> findByIdAndRoomsAndDates(@Param("rrId")Long rrId, @Param("roomId") Long roomId,
                                                   @Param("from")LocalDateTime from, @Param("to") LocalDateTime to);

    /**
     * This query return a reservation room if exists based on the inputs. The main goal of this query is to retrieve a
     * room that is being whether added or modified as part of a reservation modification, in case this exists, the idea
     * is to grab those values that are not allowed to be modified and attach them to the new entity in question.
     * @param reservation Reservation Id in mentioned
     * @param roomId Room Id in mentioned
     * @return Reservation room record if exists
     */
    Optional<ReservationRoom> findByReservationIdAndRoomId(UUID reservation, Long roomId);
}
