package com.egodoy.alten.challenge.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.List;
import java.util.UUID;

@Entity
@Table(schema = "public", name = "reservation")
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@SuperBuilder
public class Reservation extends  Audit{

    @Id
    @Column(columnDefinition = "varbinary not null")
    //@Type( type = "pg-uuid")
    private UUID id;

    @Where(clause = "status = 1")
    @OneToMany(mappedBy = "reservation")
    private List<ReservationRoom> reservationRooms;

    @Column
    private Short status;

    @Column
    private String visitorName;

    @Column
    private String visitorEmail;

    @Column
    private String visitorPhone;

    @PrePersist
    protected void onCreate() {
        setId(UUID.randomUUID());
    }

}
