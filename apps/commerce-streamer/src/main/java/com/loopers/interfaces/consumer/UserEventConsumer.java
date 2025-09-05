package com.loopers.interfaces.consumer;

import java.util.List;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import com.loopers.application.provided.UserEventLogRegister;
import com.loopers.confg.kafka.KafkaConfig;
import com.loopers.interfaces.consumer.dto.UserPayload;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserEventConsumer {
    private final UserEventLogRegister userEventLogRegister;

    @KafkaListener(
            topics = "user-event",
            groupId = "user-consumer",
            containerFactory = KafkaConfig.BATCH_LISTENER
    )
    public void userEventListener(List<UserPayload> messages, Acknowledgment acknowledgment) {
        userEventLogRegister.saveEventLog(messages);
        acknowledgment.acknowledge();
    }
}
