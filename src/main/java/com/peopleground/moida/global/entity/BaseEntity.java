package com.peopleground.moida.global.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.Clock;
import java.time.LocalDateTime;
import lombok.Getter;

@Getter
@MappedSuperclass
public class BaseEntity {

    @Column(name = "created_date", nullable = false, updatable = false)
    protected LocalDateTime createdDate;

    @Column(name = "last_modified_date")
    protected LocalDateTime lastModifiedDate;

    @Column(name = "deleted_date")
    protected LocalDateTime deletedDate;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now(Clock.systemUTC());
        this.createdDate = now;
        this.lastModifiedDate = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.lastModifiedDate = LocalDateTime.now(Clock.systemUTC());
    }

    public void delete() {
        this.deletedDate = LocalDateTime.now(Clock.systemUTC());
    }

    public boolean isDeleted() {
        return this.deletedDate != null;
    }

}
