package com.egodoy.alten.challenge.model;

import lombok.*;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(schema = "public", name = "room")
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String roomView;

    @OneToOne
    private RoomType roomType;

    @Column
    private Short status;

    @Where(clause = "status = 1")
    @OneToMany(mappedBy = "room")
    private List<ReservationRoom> reservationRooms;

}
