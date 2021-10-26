package com.egodoy.alten.challenge.model;

import com.egodoy.alten.challenge.model.listener.AuditListener;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners( AuditListener.class )
@AllArgsConstructor
@NoArgsConstructor
@Getter @Setter
@SuperBuilder
public class Audit {

    @Column
    private LocalDateTime createdAt;

    @Column
    private  LocalDateTime updatedAt;
}
