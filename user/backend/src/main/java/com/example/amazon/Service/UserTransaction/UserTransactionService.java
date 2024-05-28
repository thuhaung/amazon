package com.example.amazon.Service.UserTransaction;

import com.example.amazon.Model.UserTransaction;

import java.util.List;

public interface UserTransactionService {
    void addUserTransaction(UserTransaction transaction);
    List<UserTransaction> getUnfinishedUserTransactions();
    void removeTransactionByReferenceId(Long id);
    void finishTransaction(Long id);
}
