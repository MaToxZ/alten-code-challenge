package com.egodoy.alten.challenge.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(schema = "public", name = "reservation_room")
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@SuperBuilder
public class ReservationRoom extends Audit{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = {CascadeType.PERSIST})
    private Reservation reservation;

    @Where(clause = "status = 1")
    @ManyToOne
    private Room room;

    @Column
    private LocalDateTime stayFrom;

    @Column
    private LocalDateTime stayTo;

    @Column
    private Short status;

}
