package com.egodoy.alten.challenge.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(schema = "public", name = "at_reservation")
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@SuperBuilder
public class ReservationAuditLog extends Audit{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Integer reservationId;

    @Column
    private LocalDateTime stayFrom;

    @Column
    private LocalDateTime stayTo;

    @Column
    private Boolean status;

    @Column
    private String visitorName;

}
