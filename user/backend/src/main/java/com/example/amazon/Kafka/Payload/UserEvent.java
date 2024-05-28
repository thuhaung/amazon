package com.example.amazon.Kafka.Payload;

import lombok.*;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class UserEvent {
    private Long userId;
    private Long transactionId;
}
