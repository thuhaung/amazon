package com.example.amazon.Service;

import com.example.amazon.Exception.Data.ResourceNotFoundException;
import com.example.amazon.Model.AuthUser;
import com.example.amazon.Model.AuthUserTransaction;
import com.example.amazon.Model.Enum.AuthUserActionEnum;
import com.example.amazon.Model.Enum.TransactionStatusEnum;
import com.example.amazon.Repository.AuthUserTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthUserTransactionService {
    private final AuthUserTransactionRepository authUserTransactionRepository;

    @Transactional(readOnly=true)
    public AuthUserTransaction getTransactionById(Long id) {
        return authUserTransactionRepository.findById(id).orElseThrow(
            () -> new ResourceNotFoundException(
                String.format("No transaction can be found with id %d.", id
            ))
        );
    }

    @Transactional
    public AuthUserTransaction createNewTransaction(
        AuthUser user,
        AuthUserActionEnum transactionType
    ) {
        AuthUserTransaction transaction = AuthUserTransaction.builder()
            .type(transactionType)
            .status(TransactionStatusEnum.PENDING)
            .user(user)
            .build();

        transaction = authUserTransactionRepository.save(transaction);

        log.info(String.format(
            "New transaction created for user %1s of type %2s",
            user.getEmail(),
            transactionType.name()
        ));

        return transaction;
    }

    @Transactional
    public void updateTransactionStatus(
        Long id,
        TransactionStatusEnum status
    ) {
        authUserTransactionRepository.updateTransactionStatus(id, status);
        log.info(String.format("Updated status for transaction %d to %s.", id, status.name()));
    }

    @Transactional
    public int removeFinishedTransactions() {
        // remove successful transactions
        return authUserTransactionRepository.removeFinishedTransactions();
    }
}
