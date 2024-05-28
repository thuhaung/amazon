package com.example.amazon.Service.AuthUserTransaction;

import com.example.amazon.Model.AuthUserTransaction;
import com.example.amazon.Model.Enum.TransactionStatusEnum;

public interface AuthUserTransactionService {
    AuthUserTransaction getTransactionById(Long id);
    void removeTransactionById(Long id);
    void addAuthUserTransaction(AuthUserTransaction authUserTransaction);
    void updateAuthUserTransactionStatusByUserId(Long userId, TransactionStatusEnum status);
    int removeFinishedTransactions();
}
