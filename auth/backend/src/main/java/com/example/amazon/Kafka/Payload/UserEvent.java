package com.example.amazon.Kafka.Payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@AllArgsConstructor
@Getter
@Setter
public class UserEvent {
    private Long userId;
    private Long transactionId;
}
