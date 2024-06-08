package com.example.amazon.Service;

import com.example.amazon.Exception.Data.UserCreationException;
import com.example.amazon.Kafka.Payload.EmailPayload;
import com.example.amazon.Kafka.Payload.UserEvent;
import com.example.amazon.Kafka.Producer;
import com.example.amazon.Model.AuthUser;
import com.example.amazon.Model.AuthUserTransaction;
import com.example.amazon.Model.Enum.TransactionStatusEnum;
import com.example.amazon.Repository.AuthUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.KafkaException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class DistributedTransactionService {
    @Value("${uri.user-service}")
    private String uri;
    private final Producer producer;
    private final PollingService pollingService;
    private final AuthUserService authUserService;
    private final AuthUserRepository authUserRepository;
    private final EmailVerificationService emailVerificationService;
    private final AuthUserTransactionService authUserTransactionService;

    /**
     * notifies consumer services of new sign up event to make them sync record creation
     * @param userId - identifier of the newly created user
     * @param transactionId - identifier of the newly created transaction
     */
    @Transactional
    public void distributeSignUpEvent(Long userId, Long transactionId) {
        try {
            producer.sendUserCreatedEvent(new UserEvent(
                userId,
                transactionId
            ));

            authUserTransactionService.updateTransactionStatus(
                transactionId,
                TransactionStatusEnum.PROCESSING
            );

            HttpStatusCode statusCode = null;

            // poll for consumer confirmation of successful sync via REST API endpoint
            // keep polling if status code returns error and number of retries are fewer than 10
            statusCode = pollingService.pollForUserEvent(userId, transactionId);

            // consumer fails to sync record creation, performs manual roll back on producer side
            if (statusCode.isError()) {
                authUserTransactionService.updateTransactionStatus(
                        transactionId,
                        TransactionStatusEnum.FAILED
                );

                log.error("Consumer failed to create user.");
                throw new UserCreationException();
            }
            else {
                authUserTransactionService.updateTransactionStatus(
                        transactionId,
                        TransactionStatusEnum.DONE
                );
            }
        } catch (KafkaException e) {
            // producer fails to send event, automatically rolls back on producer side
            log.error("Unable to send creation event to user auth topic due to " + e.getMessage());
            throw new KafkaException("Unable to process registration. Please try again later.");
        }
    }

    @Transactional
    public void distributeEmailVerificationEvent(Long userId) {
        AuthUser user = authUserService.getUserById(userId);
        String token = emailVerificationService.generateToken(user);

        EmailPayload payload = EmailPayload.builder()
                .recipient(user.getEmail())
                .subject("Amazon | Verify your email")
                .text("Your email verification code is " + token)
                .build();

        producer.sendEmailVerificationToken(payload);
    }

    /**
     * rolls back when consumer service fails to create new user
     * @param userId - identifier of the newly created user
     */
    @Transactional
    public void handleFailedCreationTransaction(Long userId, Long transactionId) {
        AuthUserTransaction transaction = authUserTransactionService.getTransactionById(transactionId);

        if (transaction.getStatus() == TransactionStatusEnum.FAILED) {
            // deletes both user and transaction
            authUserRepository.deleteById(userId);
        }
    }
}
