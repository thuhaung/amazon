package com.example.amazon.Kafka;

import com.example.amazon.Kafka.Payload.UserEvent;
import com.example.amazon.Model.Enums.TransactionStatusEnum;
import com.example.amazon.Model.Enums.UserActionEnum;
import com.example.amazon.Model.User;
import com.example.amazon.Model.UserTransaction;
import com.example.amazon.Service.User.UserServiceImpl;
import com.example.amazon.Service.UserTransaction.UserTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class Consumer {
    private final UserServiceImpl userService;
    private final UserTransactionService userTransactionService;

    @KafkaListener(id="${spring.kafka.consumer.group-id}", topics="${kafka.topic.user-auth}")
    public void listenForUserAuth(
        @Payload UserEvent event,
        @Header(KafkaHeaders.RECEIVED_KEY) String key,
        Acknowledgment ack
    ) {
        log.info("Received message from user auth topic for " + key + " of user " + event.getUserId());

        if (key.equals("CREATE")) {
            User user = userService.addUserFromEvent(event.getUserId());

            UserTransaction transaction = UserTransaction.builder()
                .type(UserActionEnum.CREATE)
                .referencedAuthId(event.getTransactionId())
                .status(TransactionStatusEnum.PROCESSING)
                .user(user)
                .build();
            userTransactionService.addUserTransaction(transaction);
        }

        ack.acknowledge();
    }
}
