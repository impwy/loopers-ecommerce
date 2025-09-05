package com.loopers.application.member;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.loopers.application.provided.UserEventLogRegister;
import com.loopers.application.required.UserEventLogRepository;
import com.loopers.domain.member.CreateUserEventLog;
import com.loopers.domain.member.UserEventLog;
import com.loopers.interfaces.consumer.dto.UserPayload;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventLogModifyService implements UserEventLogRegister {
    private final UserEventLogRepository userEventLogRepository;

    @Override
    @Transactional
    public void saveEventLog(List<UserPayload> payloads) {
        payloads.forEach(payload -> {
            Optional<UserEventLog> lastLogOpt =
                    userEventLogRepository.findTopByMemberIdAndEventTypeOrderByPublishedAtDesc(
                            payload.memberId(),
                            payload.eventType()
                    );

            if (lastLogOpt.isEmpty() || payload.publishedAt().isAfter(lastLogOpt.get().getPublishedAt())) {
                UserEventLog newLog = UserEventLog.create(CreateUserEventLog.of(payload));
                userEventLogRepository.save(newLog);
                log.info("이벤트 저장됨: memberId={}, eventType={}, publishedAt={}",
                         payload.memberId(), payload.eventType(), payload.publishedAt());
            } else {
                // 기존 로그가 더 최신 → 무시
                log.info("이벤트 무시됨: memberId={}, eventType={}, publishedAt={}",
                         payload.memberId(), payload.eventType(), payload.publishedAt());
            }
        });
    }
}
