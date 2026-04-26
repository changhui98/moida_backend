package com.peopleground.moida.content.domain.entity;

import com.peopleground.moida.global.entity.AuditingEntity;
import com.peopleground.moida.user.domain.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Entity(name = "p_content")
public class Content extends AuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String body;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private int likeCount = 0;

    @Column(nullable = false)
    private int commentCount = 0;

    @Column(nullable = false)
    private long viewCount = 0;

    public static Content of(
        String body,
        User user
    ) {
        Content content = new Content();
        content.body = body;
        content.user = user;
        content.likeCount = 0;
        content.commentCount = 0;
        content.viewCount = 0;
        return content;
    }

    public void update(String body) {
        this.body = body;
    }

    public void restore() {
        this.deletedDate = null;
        this.deletedBy = null;
    }

    public void syncViewCount(long viewCount) {
        this.viewCount = viewCount;
    }
}
