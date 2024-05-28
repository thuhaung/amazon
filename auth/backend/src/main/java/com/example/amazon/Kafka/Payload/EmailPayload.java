package com.example.amazon.Kafka.Payload;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@AllArgsConstructor
@Getter
@Setter
public class EmailPayload {
    @NotEmpty
    private String recipient;
    @NotEmpty
    private String subject;
    @NotEmpty
    private String text;
}
