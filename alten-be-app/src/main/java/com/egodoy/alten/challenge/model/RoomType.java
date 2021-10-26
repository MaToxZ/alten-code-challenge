package com.egodoy.alten.challenge.model;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(schema = "public", name = "room_type")
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@Builder
public class RoomType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private Short occupants;

    @OneToMany(mappedBy = "roomType", fetch = FetchType.LAZY)
    private List<RoomTypeBed> beds;

    @OneToMany(mappedBy = "roomType")
    private List<Room> rooms;
}
