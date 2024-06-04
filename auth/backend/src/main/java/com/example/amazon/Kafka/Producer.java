package com.example.amazon.Kafka;

import com.example.amazon.Kafka.Payload.EmailPayload;
import com.example.amazon.Kafka.Payload.UserEvent;
import com.example.amazon.Model.Enum.AuthUserActionEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@RequiredArgsConstructor
public class Producer {

    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final NewTopic emailVerificationTopic;
    private final NewTopic userAuthTopic;

    public void sendEmailVerificationToken(EmailPayload payload) {
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
            emailVerificationTopic.name(),
            payload
        );

        future.whenComplete((result, error) -> {
            if (error == null) {
                log.info("Sent token to email verification topic for recipient " + payload.getRecipient() + ".");
            }
            else {
                log.error("Unable to send message to email verification topic due to " + error.getMessage());
                throw new KafkaException("Unable to send email. Please try again later.");
            }
        });
    }

    public void sendUserCreatedEvent(UserEvent event) {
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(
            userAuthTopic.name(),
            AuthUserActionEnum.CREATE.toString(),
            event
        );

        future.whenComplete((result, error) -> {
            if (error == null) {
                log.info("Sent creation event of user " + event.getUserId() + " to user auth topic.");
            }
            else {
                log.error("Unable to send creation event to user auth topic due to " + error.getMessage());
                throw new KafkaException("Unable to process registration. Please try again later.");
            }
        });
    }
}
