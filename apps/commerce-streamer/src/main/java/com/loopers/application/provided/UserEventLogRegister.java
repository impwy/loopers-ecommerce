package com.loopers.application.provided;

import java.util.List;

import com.loopers.interfaces.consumer.dto.UserPayload;

public interface UserEventLogRegister {
    void saveEventLog(List<UserPayload> payloads);
}
