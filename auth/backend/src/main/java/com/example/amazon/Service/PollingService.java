package com.example.amazon.Service;

import com.example.amazon.Exception.Data.UserCreationException;
import com.example.amazon.Model.Enum.TransactionStatusEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class PollingService {
    @Value("${uri.user-service}")
    private String uri;
    private final AuthUserTransactionService authUserTransactionService;
    private final RestTemplate restTemplate;

    @Transactional
    public HttpStatusCode pollForUserEvent(Long userId, Long transactionId) {
        int count = 0;
        HttpStatusCode statusCode = null;

        while ((statusCode == null || statusCode.isError()) && count < 10) {

            // delays each request to allow for consumer to process
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                authUserTransactionService.updateTransactionStatus(
                    transactionId,
                    TransactionStatusEnum.FAILED
                );

                log.error("Consumer failed to create user.");
                throw new UserCreationException();
            }

            try {
                ResponseEntity<String> result = restTemplate.getForEntity(
                    uri + "/poll/user/" + userId,
                    String.class
                );

                statusCode = result.getStatusCode();
            } catch (HttpClientErrorException e) {
                statusCode = e.getStatusCode();
            }

            count++;
        }

        return statusCode;
    }
}
