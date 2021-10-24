package com.egodoy.alten.challenge.model.listener;

import com.egodoy.alten.challenge.model.Audit;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

/**
 * Entity Listener to intercept object instance of Audit to be persisted or updated with the goal of populate the audit
 * fields (createdAt, updatedAt).
 */
public class AuditListener {
    @PrePersist
    private void beforePersist(Object object){
        if(object instanceof Audit){
            Audit audi = (Audit)object;
            audi.setCreatedAt( LocalDateTime.now());
            audi.setUpdatedAt(LocalDateTime.now());
        }
    }

    @PreUpdate
    private void beforeUpdate(Object object){
        if(object instanceof Audit){
            Audit audi = (Audit)object;
            audi.setCreatedAt( LocalDateTime.now());
            audi.setUpdatedAt(LocalDateTime.now());
        }
    }
}
