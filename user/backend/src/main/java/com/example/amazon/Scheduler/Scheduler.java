package com.example.amazon.Scheduler;

import com.example.amazon.Controller.Payload.Response.CustomResponse;
import com.example.amazon.Model.UserTransaction;
import com.example.amazon.Service.User.UserService;
import com.example.amazon.Service.UserTransaction.UserTransactionServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class Scheduler {
    private final UserTransactionServiceImpl userTransactionService;
    private final UserService userService;
    private final RestTemplate restTemplate;
    @Value("${uri.auth-service}")
    private String uri;

    @Scheduled(fixedRate=20000)
    public void checkTransactionWithAuth() {
        List<UserTransaction> userTransactions = userTransactionService.getUnfinishedUserTransactions();
        Long id = 0L;

        if (!userTransactions.isEmpty()) {
            for (int i = 0; i < userTransactions.size(); i++) {
                id = userTransactions.get(i).getReferencedAuthId();
                ResponseEntity<CustomResponse> result = null;

                try {
                    result = restTemplate.getForEntity(
                            uri +
                                    "/auth/transaction/" +
                                    id.toString(),
                            CustomResponse.class
                    );
                } catch (HttpClientErrorException e) {
                    CustomResponse response = e.getResponseBodyAs(CustomResponse.class);

                    result = new ResponseEntity<>(
                        response,
                        e.getStatusCode()
                    );
                }

                if (result == null || !result.getBody().getMessage().equals("DONE")) {
                    userService.removeUser(userTransactions.get(i).getUser().getId());
                    userTransactionService.removeTransactionByReferenceId(id);
                }
                else {
                    userTransactionService.finishTransaction(id);
                }
            }
        }
    }
}
