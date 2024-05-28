package com.example.amazon.Service.AuthUserTransaction;

import com.example.amazon.Exception.Data.ResourceNotFoundException;
import com.example.amazon.Model.AuthUserTransaction;
import com.example.amazon.Model.Enum.TransactionStatusEnum;
import com.example.amazon.Repository.AuthUserTransactionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthUserTransactionServiceImpl implements AuthUserTransactionService{
    private final AuthUserTransactionRepository authUserTransactionRepository;


    @Override
    public AuthUserTransaction getTransactionById(Long id) {
        return authUserTransactionRepository.findById(id).orElseThrow(
            () -> new ResourceNotFoundException("No transaction can be found with id " + id + ".")
        );
    }

    @Override
    @Transactional
    public void removeTransactionById(Long id) {
        authUserTransactionRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void addAuthUserTransaction(AuthUserTransaction authUserTransaction) {
        authUserTransactionRepository.save(authUserTransaction);
    }

    @Override
    @Transactional
    public void updateAuthUserTransactionStatusByUserId(Long userId, TransactionStatusEnum status) {
        authUserTransactionRepository.updateTransactionStatus(userId, status);
        log.info("Updated transaction's status for user id " + userId + " to " + status.name() + ".");
    }

    @Override
    public int removeFinishedTransactions() {
        return authUserTransactionRepository.removeFinishedTransactions();
    }
}
