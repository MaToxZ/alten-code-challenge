package com.egodoy.alten.challenge.model;

import com.egodoy.alten.challenge.model.enums.Bed;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(schema = "public", name = "room_type_bed")
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
public class RoomTypeBed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private RoomType roomType;

    @Column
    private Short quantity;

    @Column
    @Enumerated(EnumType.STRING)
    private Bed bedType;
}
