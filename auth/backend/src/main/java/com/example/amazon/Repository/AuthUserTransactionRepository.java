package com.example.amazon.Repository;

import com.example.amazon.Model.AuthUserTransaction;
import com.example.amazon.Model.Enum.TransactionStatusEnum;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthUserTransactionRepository extends JpaRepository<AuthUserTransaction, Long> {
    @Query("UPDATE AuthUserTransaction x SET x.status=?2 WHERE x.id=?1")
    @Modifying
    void updateTransactionStatus(Long id, TransactionStatusEnum status);
    @Query("DELETE FROM AuthUserTransaction x WHERE x.status='DONE'")
    @Modifying
    int removeFinishedTransactions();
}
