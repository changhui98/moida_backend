package com.peopleground.moida.global.entity;

import com.peopleground.moida.user.domain.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class AuditingEntity extends BaseEntity{

    @CreatedBy
    @Column(name = "created_by", nullable = false, length = 50)
    protected String createdBy;

    @LastModifiedBy
    @Column(name = "last_modified_by", length = 50)
    protected String lastModifiedBy;

    @Column(name = "deleted_by", length = 50)
    protected String deletedBy;

    public void deleteBy(User user) {
        this.deletedDate = LocalDateTime.now();
        this.deletedBy = user.getUsername();
    }

}
