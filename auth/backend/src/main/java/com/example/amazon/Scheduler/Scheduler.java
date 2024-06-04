package com.example.amazon.Scheduler;

import com.example.amazon.Service.AuthUserService;
import com.example.amazon.Service.AuthUserTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class Scheduler {

    private final AuthUserService authUserService;
    private final AuthUserTransactionService authUserTransactionService;

    @Scheduled(fixedRate=86400)
    public void removeInactiveUsers() {
        log.info("Removing inactive users...");
        int n = authUserService.removeInactiveUsers();
        log.info("Deleted " + n + " users.");
    }

    @Scheduled(fixedRate=86400)
    public void removeFinishedTransactions() {
        log.info("Removing finished transactions...");
        int n = authUserTransactionService.removeFinishedTransactions();
        log.info("Deleted " + n + " transactions.");
    }
}
