package com.egodoy.alten.challenge.repository;

import com.egodoy.alten.challenge.model.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {
    /**
     * This query returns a list of room that matches a date range for stay in the reservation table. The idea of this
     * is whether to find active rooms with no reservations or active rooms with reservations in the requested period.
     * @param from Beginning stay date
     * @param to Ending stay date
     * @param pageable Pageable object to paginate
     * @return List of rooms that match the criteria.
     */
    @Query(value = "select distinct r.* from room r " +
            "left join reservation_room rr on r.id = rr.room_id " +
            "where (" +
            "  r.status = 1" +
            "  and rr.status = 1" +
            "  and rr.stay_from between :from and :to" +
            "  and rr.stay_to between :from and :to)" +
            "  or (r.status = 1 and rr is null)",
            nativeQuery = true)
    Page<Room> findAvailabilityByFromTo(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to, Pageable pageable);

}
