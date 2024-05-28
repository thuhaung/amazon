package com.example.amazon.Service.UserTransaction;

import com.example.amazon.Exception.Data.UserTransaction.UserTransactionAlreadyExists;
import com.example.amazon.Model.UserTransaction;
import com.example.amazon.Repository.UserTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserTransactionServiceImpl implements UserTransactionService {
    private final UserTransactionRepository userTransactionRepository;

    @Override
    @Transactional
    public void addUserTransaction(UserTransaction transaction) {
        if (userTransactionRepository.existsByReferenceId(transaction.getReferencedAuthId())) {
            log.error("Transaction for referenced id " + transaction.getReferencedAuthId() + " already exists.");
            throw new UserTransactionAlreadyExists();
        }

        userTransactionRepository.save(transaction);
        log.info("Added transaction for referenced transaction id " + transaction.getReferencedAuthId() + ".");
    }

    @Override
    public List<UserTransaction> getUnfinishedUserTransactions() {
        return userTransactionRepository.getUnfinishedTransactions();
    }

    @Override
    @Transactional
    public void removeTransactionByReferenceId(Long id) {
        userTransactionRepository.removeTransactionByReferenceId(id);
        log.info("Removed transaction with referenced id " + id + ".");
    }

    @Override
    @Transactional
    public void finishTransaction(Long id) {
        userTransactionRepository.finishTransaction(id);
    }
}
