package com.loopers.domain.member;

import java.time.ZonedDateTime;

import com.loopers.domain.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "user_event_log")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserEventLog extends BaseEntity {

    @Column(name = "member_id", nullable = false)
    private String memberId;

    @Column(name = "event_id", nullable = false)
    private String eventId;

    @Column(name = "event_type", nullable = false)
    private String eventType;

    @Column(name = "published_at")
    private ZonedDateTime publishedAt;

    private UserEventLog(String memberId, String eventId, String eventType, ZonedDateTime publishedAt) {
        this.memberId = memberId;
        this.eventId = eventId;
        this.eventType = eventType;
        this.publishedAt = publishedAt;
    }

    public static UserEventLog create(CreateUserEventLog createSpec) {
        return new UserEventLog(createSpec.memberId(), createSpec.eventId(),
                                createSpec.eventType(), createSpec.publishedAt());
    }
}
